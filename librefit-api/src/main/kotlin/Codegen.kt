@file:JvmName("ApiCodegen")
package io.tohuwabohu.librefit.api.codegen

import kotlinx.serialization.json.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

val typeMap = mapOf<String, String>(
    "integer" to "Number",
    "number" to "Number",
    "string" to "String",
    "array<string>" to "Array<String>"
)

fun main(args: Array<String>) {
    if (args.size < 2) {
        throw IllegalArgumentException("No parameters supplied. First one should be the spec path, second one the output directory.")
    }

    val spec = readJsonFromDisk(args[0])["spec"]!!.jsonObject

    val schemasJson = spec["components"]!!.jsonObject["schemas"]!!.jsonObject
    val pathsJson = spec["paths"]!!.jsonObject

    val enums = collectEnums(schemasJson)
    val refs = collectRefs(schemasJson)
    val schemas = collectSchemas(schemasJson)
    val operations = collectOperations(pathsJson)

    println(enums.toString())
    println(refs.toString())
    println(schemas.toString())
    println(operations.toString())

    writeJs(args[1], enums, refs, schemas, operations)
}

fun writeJs(out: String, enums: List<Enum>, refs: List<Ref>, schemas: List<Schema>, operations: Map<String, Operation>) {
    val modelOut = File("$out/api/model.js")
    val apiOut = File("$out/api/index.js")

    if (modelOut.exists()) {
        modelOut.delete()
    }

    modelOut.createNewFile()

    if (apiOut.exists()) {
        apiOut.delete()
    }

    apiOut.createNewFile()

    val modelWriter: BufferedWriter = modelOut.bufferedWriter()
    val apiWriter: BufferedWriter = apiOut.bufferedWriter()

    enums.forEach { enum ->
        val enumValues = enum.allowedValues.joinToString(",\n") { value ->
            "\t${value.lowercase().capitalize()}: '${value.uppercase()}'"
        }

        modelWriter.write("/**\n")
        modelWriter.write(" * @readonly\n")
        modelWriter.write(" * @enum {String}\n")
        modelWriter.write(" */\n")
        modelWriter.write("export const ${enum.name} = {\n")
        modelWriter.write(enumValues)
        modelWriter.write("\n}\n\n")
    }

    refs.forEach { ref ->
        modelWriter.write("/**\n")
        modelWriter.write(" * @typedef {Object} ${ref.name}\n")
        modelWriter.write(" */\n\n")
    }

    schemas.forEach{ schema ->
        val properties = schema.properties
        val mandatory = schema.mandatory
        val declaration = properties.entries.joinToString("\n") { (k, v) ->
            if (mandatory?.contains(k) == true) " * @property {$v} $k" else " * @property {$v} [$k]"
        }

        modelWriter.write("/**\n")
        modelWriter.write(" * @typedef {Object} ${schema.name}\n")
        modelWriter.write(declaration)
        modelWriter.write("\n")
        modelWriter.write(" */\n\n")
    }

    apiWriter.write("export const api = {\n")

    operations.keys.forEach { operationId ->
        val operation = operations[operationId]!!

        apiWriter.write("\t$operationId: {\n")
        apiWriter.write("\t\tpath: '${operation.path}',\n")

        if (operation.schema?.name != null) {
            apiWriter.write("\t\t/** @see ${operation.schema.name} */\n")
        }

        apiWriter.write("\t\tcontentType: '${operation.content}',\n")
        apiWriter.write("\t\tmethod: '${operation.method}',\n")
        apiWriter.write("\t\tguarded: ${operation.guarded}\n")
        apiWriter.write("\t},\n")
    }

    apiWriter.write("}\n")

    modelWriter.close()
    apiWriter.close()
}

fun collectEnums(schemas: JsonObject): List<Enum> {
    return schemas.keys.filter{ schemaName ->
        schemas[schemaName]!!.jsonObject["enum"] != null
    }.map { schemaName ->
        val element = schemas[schemaName]!!.jsonObject

        val enumElement = element["enum"]

        val enum = enumElement!!.jsonArray.map { it.toString().replace("\"", "") }

        Enum(schemaName, enum)
    }.toList()
}

fun collectRefs(schemas: JsonObject): List<Ref> {
    return schemas.keys.filter {schemaName ->
        schemas[schemaName]!!.jsonObject["format"] != null
    }.map {schemaName ->
        val element = schemas[schemaName]!!.jsonObject
        
        Ref(schemaName, element["type"]!!.jsonPrimitive.content, element["format"]?.jsonPrimitive?.content)
    }
}

fun collectSchemas(schemas: JsonObject): List<Schema> {
    return schemas.keys.filter { schemaName ->
        schemas[schemaName]!!.jsonObject["properties"] != null
    }.map { schemaName ->
        val element = schemas[schemaName]!!.jsonObject

        val propertiesElement = element["properties"]!!

        val properties = propertiesElement.jsonObject.keys.associateWith { property ->
            val typeOrRefElement = propertiesElement.jsonObject[property]!!.jsonObject

            var typeOrRef = typeOrRefElement.jsonObject["type"]?.jsonPrimitive?.content

            if (typeOrRef == null) {
                val ref = typeOrRefElement.jsonObject["\$ref"]!!.jsonPrimitive.content

                typeOrRef = ref.substringAfterLast("/")
            }

            if (typeOrRef == "array") {
                val arrayType = typeOrRefElement.jsonObject["items"]!!.jsonObject["type"]
                val arrayRef = typeOrRefElement.jsonObject["items"]!!.jsonObject["\$ref"]

                val arrayTypeOrArrayRef = arrayType ?: arrayRef;

                typeOrRef = "Array<${typeMap[arrayTypeOrArrayRef!!.jsonPrimitive.content] ?: arrayTypeOrArrayRef.jsonPrimitive.content.substringAfterLast("/")}>"
            }

            typeMap[typeOrRef] ?: typeOrRef
        }

        val required: List<String>? = element["required"]?.jsonArray?.map { it.toString().replace("\"", "") }?.toList()

        Schema(schemaName, properties, required)
    }.toList()
}

fun collectOperations(paths: JsonObject): Map<String, Operation> {
    return paths.keys.associate { path ->
        val pathElement = paths[path]!!.jsonObject

        val scraped = pathElement.keys.flatMap { method ->
            val methodElement = pathElement[method]!!.jsonObject

            val operationElement = methodElement["operationId"]
            val operation = operationElement?.jsonPrimitive?.content ?: method.plus(path.pathToCamelCase())

            val contentElement = methodElement["requestBody"]?.jsonObject?.get("content")?.jsonObject
            val contentType = if (contentElement != null) {
                contentElement.jsonObject.keys.toList()[0]
            } else "text/plain"

            val schema =
                contentElement?.get(contentType)?.jsonObject?.get("schema")
                    ?.jsonObject?.get("\$ref")?.jsonPrimitive?.content?.substringAfterLast("/")

            val responsesElement = methodElement["responses"]
            val guarded = if(responsesElement?.jsonObject?.keys?.contains("401") == true) "y" else "n"

            listOf(operation, method, contentType, guarded, schema)
        }

        val schema = if (scraped[4] != null) {
            Schema(scraped[4]!!, mapOf(), listOf())
        } else null

        val operation = Operation(scraped[0]!!, path, scraped[1]!!, scraped[2]!!, scraped[3]!! == "y", schema)

        operation.id to operation
    }
}

fun readJsonFromDisk(path: String): JsonObject {
    val inputStream: InputStream = FileInputStream(path)
    val json = inputStream.bufferedReader().use{it.readText()}

    val jsonElement = Json.parseToJsonElement(json)

    return JsonObject(mapOf("spec" to jsonElement))
}

fun String.pathToCamelCase(): String {
    val pattern = "/\\{?[a-z]".toRegex()
    return replace(pattern) { it.value.last().uppercase() }.replace("}", "")
}

fun String.capitalize(): String {
    return replaceFirstChar(Char::titlecase)
}

data class Schema(val name: String, val properties: Map<String, String>, val mandatory: List<String>?)
data class Enum(val name: String, val allowedValues: List<String>)
data class Ref(val name: String, val type: String, val format: String?)
data class Operation(val id: String, val path: String, val method: String, val content: String, val guarded: Boolean, val schema: Schema?)