package io.tohuwabohu.router

import io.vertx.ext.web.Router
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import java.util.function.Predicate
import java.util.regex.Pattern

/**
 * Currently the Quinoa SPA routing does not work with RESTEasy classic and needs a workaround
 */
@ApplicationScoped
class SPARouting {
    private val pathPrefixes = setOf("/q/", "/api/", "/@")
    private val fileNamePredicate: Predicate<String> = Pattern.compile(".+\\.[a-zA-Z0-9]+$").asMatchPredicate()

    fun init(@Observes router: Router) {
        router.get("/*").handler{ routingContext ->
            val path = routingContext.normalizedPath()

            if (!path.equals("/") && pathPrefixes.stream().noneMatch(path::startsWith) && !fileNamePredicate.test(path)) {
                routingContext.reroute("/")
            } else {
                routingContext.next()
            }
        }
    }
}