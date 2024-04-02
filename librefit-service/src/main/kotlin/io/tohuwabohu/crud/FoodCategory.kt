package io.tohuwabohu.crud

import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class FoodCategory (
    @Id
    @Column(unique = true, nullable = false)
    var shortvalue: String,

    @Column(nullable = false)
    var longvalue: String,

    @Column(nullable = false)
    var visible: Boolean = true,
)

@ApplicationScoped
class FoodCategoryRepository : PanacheRepositoryBase<FoodCategory, String> {
    fun listVisibleCategories(): Uni<List<FoodCategory>> {
        return list("where visible = ?1", true)
    }
}