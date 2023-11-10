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
        if (ConfigUtils.getProfiles().contains("dev")){
            VertxContextSupport.subscribeAndAwait {
                sf.withTransaction { session ->
                    session.persistAll(
                        LibreUser(
                            null,
                            "test1@test.dev",
                            "test1",
                            "User",
                            "Testuser 1",
                            avatar = "/assets/images/avatars/dog-1.png"
                        ),
                        LibreUser(
                            null,
                            "test2@test.dev",
                            "test2",
                            "User",
                            "Testuser 2",
                            avatar = "/assets/images/avatars/buffdude-1.png"
                        ),
                        LibreUser(
                            null,
                            "test3@test.dev",
                            "test3",
                            "User",
                            "Testuser 3",
                            avatar = "/assets/images/avatars/lady-1.png"
                        )
                    )
                }
            }
        }
    }
}