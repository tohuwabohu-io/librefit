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
    val outFile = File(out)

    if (outFile.exists()) {
        outFile.delete()
    }

    outFile.createNewFile()

    val writer: BufferedWriter = outFile.bufferedWriter()

    enums.forEach { enum ->
        val enumValues = enum.allowedValues.joinToString("|") { it }

        writer.write("/**\n")
        writer.write(" * @typedef {$enumValues} ${enum.name}\n")
        writer.write("*/\n\n")
    }

    refs.forEach { ref ->
        writer.write("/**\n")
        writer.write(" * @typedef {Object} ${ref.name}\n")
        writer.write(" */\n\n")
    }

    schemas.forEach{ schema ->
        val properties = schema.properties
        val mandatory = schema.mandatory
        val declaration = properties.entries.joinToString("\n") { (k, v) ->
            if (mandatory?.contains(k) == true) " * @property {$v} $k" else " * @property [$v] $k"
        }

        writer.write("/**\n")
        writer.write(" * @typedef {Object} ${schema.name}\n")
        writer.write(declaration)
        writer.write("\n")
        writer.write(" */\n\n")
    }

    writer.write("export const api = {\n")

    operations.keys.forEach { operationId ->
        val operation = operations[operationId]!!

        writer.write("\t$operationId: {\n")
        writer.write("\t\tpath: '${operation.path}',\n")

        if (operation.schema?.name != null) {
            writer.write("\t\t/** @see ${operation.schema.name} */\n")
        }

        writer.write("\t\tcontentType: '${operation.content}',\n")
        writer.write("\t\tmethod: '${operation.method}'\n")
        writer.write("\t},\n")
    }

    writer.write("}\n")

    writer.close()
}

fun collectEnums(schemas: JsonObject): List<Enum> {
    return schemas.keys.filter{ schemaName ->
        schemas[schemaName]!!.jsonObject["enum"] != null
    }.map { schemaName ->
        val element = schemas[schemaName]!!.jsonObject

        val enumElement = element["enum"]

        val enum = enumElement!!.jsonArray.map { it.toString() }

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

            listOf(operation, method, contentType, schema)
        }

        val schema = if (scraped[3] != null) {
            Schema(scraped[3]!!, mapOf(), listOf())
        } else null

        val operation = Operation(scraped[0]!!, path, scraped[1]!!, scraped[2]!!, schema)

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

data class Schema(val name: String, val properties: Map<String, String>, val mandatory: List<String>?)
data class Enum(val name: String, val allowedValues: List<String>)
data class Ref(val name: String, val type: String, val format: String?)
data class Operation(val id: String, val path: String, val method: String, val content: String, val schema: Schema?)