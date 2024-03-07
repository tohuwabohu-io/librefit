package io.tohuwabohu

import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.vertx.RunOnVertxContext
import io.quarkus.test.vertx.UniAsserter
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.tohuwabohu.calc.*
import jakarta.inject.Inject
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@QuarkusTest
@TestHTTPEndpoint(TdeeResource::class)
class TdeeResourceTest {

    @Inject
    private lateinit var tdeeCalculator: TdeeCalculator

    @Test
    fun `should calculate weight loss for men`() {
        val params = Tdee(
            age = 30,
            weight = 90,
            height = 180,
            weeklyDifference = 5,
            activityLevel = 1.5f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.MALE
        )

        val tdee: Tdee = When {
            get("/calculate/${params.age}/${params.sex}/${params.weight}/${params.height}/${params.activityLevel}/${params.weeklyDifference}/${params.calculationGoal}")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(Tdee::class.java)
        }

        Assertions.assertEquals(tdee.bmr, 1995f)
        Assertions.assertEquals(tdee.deficit, 500f)
        Assertions.assertEquals(tdee.bmi, 28f)
        Assertions.assertEquals(tdee.tdee, 2992.0)
        Assertions.assertEquals(tdee.bmiCategory, BmiCategory.OVERWEIGHT)
        Assertions.assertEquals(tdee.targetBmi[0], 20)
        Assertions.assertEquals(tdee.targetBmi[1], 25)
        Assertions.assertEquals(tdee.targetWeight, 73f)
        Assertions.assertEquals(tdee.target, 2492f)
        Assertions.assertEquals(tdee.durationDays, 238.0)
    }

    @Test
    fun `should calculate weight gain for women`() {
        val params = Tdee(
            age = 25,
            weight = 52,
            height = 155,
            weeklyDifference = 1,
            activityLevel = 1.25f,
            calculationGoal = CalculationGoal.GAIN,
            sex = CalculationSex.FEMALE
        )

        val tdee: Tdee = When {
            get("/calculate/${params.age}/${params.sex}/${params.weight}/${params.height}/${params.activityLevel}/${params.weeklyDifference}/${params.calculationGoal}")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(Tdee::class.java)
        }

        Assertions.assertEquals(tdee.bmr, 1316f)
        Assertions.assertEquals(tdee.deficit, 100f)
        Assertions.assertEquals(tdee.bmi, 22f)
        Assertions.assertEquals(tdee.tdee, 1645.0)
        Assertions.assertEquals(tdee.bmiCategory, BmiCategory.STANDARD_WEIGHT)
        Assertions.assertEquals(tdee.targetBmi[0], 20)
        Assertions.assertEquals(tdee.targetBmi[1], 25)
        Assertions.assertEquals(tdee.targetWeight, 54f)
        Assertions.assertEquals(tdee.target, 1745f)
        Assertions.assertEquals(tdee.durationDays, 140.0)
    }

    @Test
    fun `should fail with invalid age`() {
        val params = Tdee(
            age = 13,
            weight = 45,
            height = 160,
            weeklyDifference = 0,
            activityLevel = 1.5f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.FEMALE
        )

        When {
            get("/calculate/${params.age}/${params.sex}/${params.weight}/${params.height}/${params.activityLevel}/${params.weeklyDifference}/${params.calculationGoal}")
        } Then {
            statusCode(400)
            body("errors[0].field", Matchers.equalTo("age"))
        }
    }

    @Test
    fun `should fail with invalid weight`() {
        val params = Tdee(
            age = 30,
            weight = 20,
            height = 160,
            weeklyDifference = 0,
            activityLevel = 1.5f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.FEMALE
        )

        When {
            get("/calculate/${params.age}/${params.sex}/${params.weight}/${params.height}/${params.activityLevel}/${params.weeklyDifference}/${params.calculationGoal}")
        } Then {
            statusCode(400)
            body("errors[0].field", Matchers.equalTo("weight"))
        }
    }

    @Test
    fun `should fail with invalid height`() {
        val params = Tdee(
            age = 30,
            weight = 45,
            height = 340,
            weeklyDifference = 0,
            activityLevel = 1.5f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.FEMALE
        )

        When {
            get("/calculate/${params.age}/${params.sex}/${params.weight}/${params.height}/${params.activityLevel}/${params.weeklyDifference}/${params.calculationGoal}")
        } Then {
            statusCode(400)
            body("errors[0].field", Matchers.equalTo("height"))
        }
    }

    @Test
    fun `should fail with invalid weekly difference`() {
        @Test
        fun `should fail with invalid height`() {
            val params = Tdee(
                age = 30,
                weight = 45,
                height = 160,
                weeklyDifference = 8,
                activityLevel = 1.5f,
                calculationGoal = CalculationGoal.GAIN,
                sex = CalculationSex.FEMALE
            )

            When {
                get("/calculate/${params.age}/${params.sex}/${params.weight}/${params.height}/${params.activityLevel}/${params.weeklyDifference}/${params.calculationGoal}")
            } Then {
                statusCode(400)
                body("errors[0].field", Matchers.equalTo("weeklyDifference"))
            }
        }
    }

    @Test
    fun `should fail with invalid activity level`() {
        val params = Tdee(
            age = 30,
            weight = 45,
            height = 160,
            weeklyDifference = 0,
            activityLevel = 0.5f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.FEMALE
        )

        When {
            get("/calculate/${params.age}/${params.sex}/${params.weight}/${params.height}/${params.activityLevel}/${params.weeklyDifference}/${params.calculationGoal}")
        } Then {
            statusCode(400)
            body("errors[0].field", Matchers.equalTo("activityLevel"))
        }
    }

    @Test
    fun `should fail with 404`() {
        var params = Tdee(
            age = -30,
            weight = 45,
            height = 160,
            weeklyDifference = 0,
            activityLevel = 0.5f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.FEMALE
        )

        When {
            get("/calculate/${params.age}/${params.sex}/${params.weight}/${params.height}/${params.activityLevel}/${params.weeklyDifference}/${params.calculationGoal}")
        } Then {
            statusCode(404)
        }

        params = Tdee(
            age = 30,
            weight = -45,
            height = 160,
            weeklyDifference = 0,
            activityLevel = 0.5f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.FEMALE
        )

        When {
            get("/calculate/${params.age}/${params.sex}/${params.weight}/${params.height}/${params.activityLevel}/${params.weeklyDifference}/${params.calculationGoal}")
        } Then {
            statusCode(404)
        }

        params = Tdee(
            age = 30,
            weight = 45,
            height = -160,
            weeklyDifference = 0,
            activityLevel = 0.5f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.FEMALE
        )

        When {
            get("/calculate/${params.age}/${params.sex}/${params.weight}/${params.height}/${params.activityLevel}/${params.weeklyDifference}/${params.calculationGoal}")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @RunOnVertxContext
    fun `should calculate and return underweight, obese and severely obese for men`(uniAsserter: UniAsserter) {
        val underweight1 = Tdee(
            age = 25,
            weight = 60,
            height = 180,
            weeklyDifference = 0,
            activityLevel = 1f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.MALE
        )

        val obese1 = Tdee(
            age = 25,
            weight = 130,
            height = 180,
            weeklyDifference = 0,
            activityLevel = 1f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.MALE
        )

        val obese2 = Tdee(
            age = 45,
            weight = 150,
            height = 180,
            weeklyDifference = 0,
            activityLevel = 1f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.MALE
        )


        uniAsserter.assertThat(
            { tdeeCalculator.calculate(underweight1)},
            { result -> Assertions.assertEquals(BmiCategory.UNDERWEIGHT, result.bmiCategory)}
        )

        uniAsserter.assertThat(
            { tdeeCalculator.calculate(obese1) },
            { result -> Assertions.assertEquals(BmiCategory.OBESE, result.bmiCategory)}
        )

        uniAsserter.assertThat(
            { tdeeCalculator.calculate(obese2) },
            { result -> Assertions.assertEquals(BmiCategory.SEVERELY_OBESE, result.bmiCategory)}
        )
    }

    @Test
    @RunOnVertxContext
    fun `should calculate and return underweight, obese and severely obese for women`(uniAsserter: UniAsserter) {
        val underweight1 = Tdee(
            age = 18,
            weight = 40,
            height = 150,
            weeklyDifference = 0,
            activityLevel = 1f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.FEMALE
        )

        val obese1 = Tdee(
            age = 30,
            weight = 80,
            height = 160,
            weeklyDifference = 0,
            activityLevel = 1f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.FEMALE
        )

        val obese2 = Tdee(
            age = 45,
            weight = 120,
            height = 165,
            weeklyDifference = 0,
            activityLevel = 1f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.FEMALE
        )


        uniAsserter.assertThat(
            { tdeeCalculator.calculate(underweight1)},
            { result -> Assertions.assertEquals(BmiCategory.UNDERWEIGHT, result.bmiCategory)}
        )

        uniAsserter.assertThat(
            { tdeeCalculator.calculate(obese1) },
            { result -> Assertions.assertEquals(BmiCategory.OBESE, result.bmiCategory)}
        )

        uniAsserter.assertThat(
            { tdeeCalculator.calculate(obese2) },
            { result -> Assertions.assertEquals(BmiCategory.SEVERELY_OBESE, result.bmiCategory)}
        )
    }
}