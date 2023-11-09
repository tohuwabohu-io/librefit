package io.tohuwabohu.security

import io.quarkus.runtime.StartupEvent
import io.quarkus.runtime.configuration.ConfigUtils
import io.quarkus.vertx.VertxContextSupport
import io.tohuwabohu.crud.LibreUser
import jakarta.enterprise.event.Observes
import jakarta.inject.Singleton
import org.hibernate.reactive.mutiny.Mutiny


@Singleton
class Startup {
    fun loadUsers(@Observes event: StartupEvent, sf: Mutiny.SessionFactory) {
        if (ConfigUtils.getProfiles().contains("dev")) {
            VertxContextSupport.subscribeAndAwait {
                sf.withTransaction { session ->
                    session.persistAll(
                        LibreUser(
                            0L,
                            "test1@test.dev",
                            "test1",
                            "User",
                            "Testuser 1"
                        ),
                        LibreUser(
                            0L,
                            "test2@test.dev",
                            "test2",
                            "User",
                            "Testuser 2"
                        ),
                        LibreUser(
                            0L,
                            "test3@test.dev",
                            "test3",
                            "User",
                            "Testuser 3"
                        )
                    )
                }
            }
        }
    }
}