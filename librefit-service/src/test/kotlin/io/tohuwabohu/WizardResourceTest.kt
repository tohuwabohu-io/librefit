package io.tohuwabohu

import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.vertx.RunOnVertxContext
import io.quarkus.test.vertx.UniAsserter
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.tohuwabohu.calc.BmiCategory
import io.tohuwabohu.calc.CalculationGoal
import io.tohuwabohu.calc.CalculationSex
import io.tohuwabohu.calc.Wizard
import io.tohuwabohu.calc.WizardInput
import io.tohuwabohu.calc.WizardResult
import io.tohuwabohu.calc.WizardTargetDateInput
import io.tohuwabohu.calc.WizardTargetDateResult
import io.tohuwabohu.calc.WizardTargetWeightInput
import io.tohuwabohu.calc.WizardTargetWeightResult
import jakarta.inject.Inject
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

@QuarkusTest
@TestHTTPEndpoint(WizardResource::class)
class WizardResourceTest {

    @Inject
    private lateinit var wizard: Wizard

    private val calculationStartDate = LocalDate.of(2024, 5, 1)

    @Test
    fun `should calculate weight loss for men`() {
        val params = WizardInput(
            age = 30,
            weight = 90,
            height = 180,
            weeklyDifference = 5,
            activityLevel = 1.5f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.MALE
        )

        val wizardResult: WizardResult = When {
            get("/calculate/${params.age}/${params.sex}/${params.weight}/${params.height}/${params.activityLevel}/${params.weeklyDifference}/${params.calculationGoal}")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(WizardResult::class.java)
        }

        Assertions.assertEquals(1995f, wizardResult.bmr)
        Assertions.assertEquals(500f, wizardResult.deficit)
        Assertions.assertEquals(28f, wizardResult.bmi)
        Assertions.assertEquals(2992.0, wizardResult.tdee)
        Assertions.assertEquals(BmiCategory.OVERWEIGHT, wizardResult.bmiCategory)
        Assertions.assertEquals(20, wizardResult.targetBmiLower)
        Assertions.assertEquals(25, wizardResult.targetBmiUpper)
        Assertions.assertEquals(65f, wizardResult.targetWeightLower)
        Assertions.assertEquals(81f, wizardResult.targetWeightUpper)
        Assertions.assertEquals(73f, wizardResult.targetWeight)
        Assertions.assertEquals(2492f, wizardResult.target)
        Assertions.assertEquals(238.0, wizardResult.durationDays)
    }

    @Test
    fun `should calculate weight gain for women`() {
        val params = WizardInput(
            age = 25,
            weight = 52,
            height = 155,
            weeklyDifference = 1,
            activityLevel = 1.25f,
            calculationGoal = CalculationGoal.GAIN,
            sex = CalculationSex.FEMALE
        )

        val wizardResult: WizardResult = When {
            get("/calculate/${params.age}/${params.sex}/${params.weight}/${params.height}/${params.activityLevel}/${params.weeklyDifference}/${params.calculationGoal}")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(WizardResult::class.java)
        }

        Assertions.assertEquals(1316f, wizardResult.bmr)
        Assertions.assertEquals(100f, wizardResult.deficit)
        Assertions.assertEquals(22f, wizardResult.bmi)
        Assertions.assertEquals(1645.0, wizardResult.tdee)
        Assertions.assertEquals(BmiCategory.STANDARD_WEIGHT, wizardResult.bmiCategory)
        Assertions.assertEquals(20, wizardResult.targetBmiLower)
        Assertions.assertEquals(25, wizardResult.targetBmiUpper)
        Assertions.assertEquals(22, wizardResult.targetBmi)
        Assertions.assertEquals(48f, wizardResult.targetWeightLower)
        Assertions.assertEquals(60f, wizardResult.targetWeightUpper)
        Assertions.assertEquals(54f, wizardResult.targetWeight)
        Assertions.assertEquals(1745f, wizardResult.target)
        Assertions.assertEquals(140.0, wizardResult.durationDays)
    }

    @Test
    fun `should fail with invalid age`() {
        val params = WizardInput(
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
        val params = WizardInput(
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
        val params = WizardInput(
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
            val params = WizardInput(
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
        val params = WizardInput(
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
        var params = WizardInput(
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

        params = WizardInput(
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

        params = WizardInput(
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
    fun `should calculate for target date`() {
        val targetDate = LocalDate.now().plusDays(150)

        val wizardResult: WizardTargetDateResult = When {
            get("/custom/date/30/180/90/MALE/${targetDate}/LOSS")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(WizardTargetDateResult::class.java)
        }

        Assertions.assertNotNull(wizardResult.resultByRate)
        Assertions.assertFalse(wizardResult.resultByRate.isEmpty())
    }

    @Test
    fun `should calculate for target weight`() {
        val wizardResult: WizardTargetWeightResult = When {
            get("/custom/weight/30/180/90/MALE/80")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(WizardTargetWeightResult::class.java)
        }

        Assertions.assertNotNull(wizardResult.datePerRate)
        Assertions.assertFalse(wizardResult.datePerRate.isEmpty())
    }

    @Test
    fun `should fail with invalid target weight`() {
        When {
            get("/custom/weight/30/180/90/MALE/400") // invalid because it is larger than maximum weight
        } Then {
            statusCode(400)
            body("errors[0].field", Matchers.equalTo("targetWeight"))
        }
    }

    @Test
    @RunOnVertxContext
    fun `should calculate and return underweight, obese and severely obese for men`(uniAsserter: UniAsserter) {
        val underweight1 = WizardInput(
            age = 25,
            weight = 60,
            height = 180,
            weeklyDifference = 0,
            activityLevel = 1f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.MALE
        )

        val obese1 = WizardInput(
            age = 25,
            weight = 130,
            height = 180,
            weeklyDifference = 0,
            activityLevel = 1f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.MALE
        )

        val obese2 = WizardInput(
            age = 45,
            weight = 150,
            height = 180,
            weeklyDifference = 0,
            activityLevel = 1f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.MALE
        )


        uniAsserter.assertThat(
            { wizard.calculate(underweight1)},
            { result -> Assertions.assertEquals(BmiCategory.UNDERWEIGHT, result.bmiCategory)}
        )

        uniAsserter.assertThat(
            { wizard.calculate(obese1) },
            { result -> Assertions.assertEquals(BmiCategory.OBESE, result.bmiCategory)}
        )

        uniAsserter.assertThat(
            { wizard.calculate(obese2) },
            { result -> Assertions.assertEquals(BmiCategory.SEVERELY_OBESE, result.bmiCategory)}
        )
    }

    @Test
    @RunOnVertxContext
    fun `should calculate and return underweight, obese and severely obese for women`(uniAsserter: UniAsserter) {
        val underweight1 = WizardInput(
            age = 18,
            weight = 40,
            height = 150,
            weeklyDifference = 0,
            activityLevel = 1f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.FEMALE
        )

        val obese1 = WizardInput(
            age = 30,
            weight = 80,
            height = 160,
            weeklyDifference = 0,
            activityLevel = 1f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.FEMALE
        )

        val obese2 = WizardInput(
            age = 45,
            weight = 120,
            height = 165,
            weeklyDifference = 0,
            activityLevel = 1f,
            calculationGoal = CalculationGoal.LOSS,
            sex = CalculationSex.FEMALE
        )


        uniAsserter.assertThat(
            { wizard.calculate(underweight1)},
            { result -> Assertions.assertEquals(BmiCategory.UNDERWEIGHT, result.bmiCategory)}
        )

        uniAsserter.assertThat(
            { wizard.calculate(obese1) },
            { result -> Assertions.assertEquals(BmiCategory.OBESE, result.bmiCategory)}
        )

        uniAsserter.assertThat(
            { wizard.calculate(obese2) },
            { result -> Assertions.assertEquals(BmiCategory.SEVERELY_OBESE, result.bmiCategory)}
        )
    }

    @Test
    @RunOnVertxContext
    fun `should warn for underweight target`(uniAsserter: UniAsserter) {
        uniAsserter.assertThat(
            { wizard.calculateForTargetWeight(
                WizardTargetWeightInput(
                    age = 30,
                    sex = CalculationSex.MALE,
                    currentWeight = 60f,
                    height = 170,
                    targetWeight = 50f,
                    startDate = calculationStartDate
                ))
            },
            { result ->
                Assertions.assertEquals(BmiCategory.UNDERWEIGHT, result.targetClassification)
                Assertions.assertEquals(true, result.warning)
                Assertions.assertEquals("Your target weight will classify you as underweight. You should revisit your choice.", result.message)
            }
        )
    }

    @Test
    @RunOnVertxContext
    fun `should warn for obese target`(uniAsserter: UniAsserter) {
        uniAsserter.assertThat(
            { wizard.calculateForTargetWeight(
                WizardTargetWeightInput(
                    age = 30,
                    sex = CalculationSex.MALE,
                    currentWeight = 60f,
                    height = 170,
                    targetWeight = 110f,
                    startDate = calculationStartDate
                )
            ) },
            { result ->
                Assertions.assertEquals(BmiCategory.OBESE, result.targetClassification)
                Assertions.assertEquals(true, result.warning)
                Assertions.assertEquals("Your target weight will classify you as obese. You should revisit your choice.", result.message)
            }
        )
    }

    @Test
    @RunOnVertxContext
    fun `should warn for severely obese target`(uniAsserter: UniAsserter) {
        uniAsserter.assertThat(
            { wizard.calculateForTargetWeight(
                WizardTargetWeightInput(
                    age = 30,
                    sex = CalculationSex.MALE,
                    currentWeight = 60f,
                    height = 170,
                    targetWeight = 150f,
                    startDate = calculationStartDate
                )
            ) },
            { result ->
                Assertions.assertEquals(BmiCategory.SEVERELY_OBESE, result.targetClassification)
                Assertions.assertEquals(true, result.warning)
                Assertions.assertEquals("Your target weight will classify you as severely obese. You should revisit your choice.", result.message)
            }
        )
    }

    @Test
    @RunOnVertxContext
    fun `should calculate weight loss duration`(uniAsserter: UniAsserter) {
        val expected = mapOf(
            100 to 1190,
            200 to 595,
            300 to 397,
            400 to 298,
            500 to 238,
            600 to 198,
            700 to 170
        )

        uniAsserter.assertThat(
            { wizard.calculateForTargetWeight(
                WizardTargetWeightInput(
                    age = 30,
                    sex = CalculationSex.MALE,
                    currentWeight = 100f,
                    height = 170,
                    targetWeight = 83f,
                    startDate = calculationStartDate
                )
            ) },
            { result ->
                Assertions.assertEquals(BmiCategory.OVERWEIGHT, result.targetClassification)
                Assertions.assertEquals(false, result.warning)
                Assertions.assertEquals("", result.message)

                expected.forEach { (rate, differenceDays) -> Assertions.assertEquals(calculationStartDate.plusDays(differenceDays.toLong()), result.datePerRate[rate]) }
            }
        )
    }

    @Test
    @RunOnVertxContext
    fun `should calculate weight gain duration`(uniAsserter: UniAsserter) {
        val expected = mapOf(
            100 to 350,
            200 to 175,
            300 to 117,
            400 to 88,
            500 to 70,
            600 to 58,
            700 to 50
        )

        uniAsserter.assertThat(
            { wizard.calculateForTargetWeight(
                WizardTargetWeightInput(
                    age = 30,
                    sex = CalculationSex.FEMALE,
                    currentWeight = 50f,
                    height = 155,
                    targetWeight = 45f,
                    startDate = calculationStartDate
                )
            ) },
            { result ->
                Assertions.assertEquals(BmiCategory.STANDARD_WEIGHT, result.targetClassification)
                Assertions.assertEquals(false, result.warning)
                Assertions.assertEquals("", result.message)

                expected.forEach { (rate, differenceDays) -> Assertions.assertEquals(calculationStartDate.plusDays(differenceDays.toLong()), result.datePerRate[rate]) }
            }
        )
    }

    @Test
    @RunOnVertxContext
    fun `should calculate target weights for specific weight loss goal`(uniAsserter: UniAsserter) {
        val targetDate = LocalDate.now().plusDays(250) // suppose we set the target date 250 days later
        val rates = listOf(100, 200, 300, 400, 500)

        val expectedWeightByRate = mapOf(
            100 to 82f,
            200 to 78f,
            300 to 75f,
            400 to 71f,
            500 to 68f
        )

        val expectedBmiByRate = mapOf(
            100 to 28f,
            200 to 27f,
            300 to 26f,
            400 to 25f,
            500 to 24f
        )

        uniAsserter.assertThat(
            { wizard.calculateForTargetDate(WizardTargetDateInput(
                age = 30,
                height = 170,
                currentWeight = 85f,
                sex = CalculationSex.MALE,
                targetDate = targetDate,
                calculationGoal = CalculationGoal.LOSS)
            ) },
            { result ->
                rates.forEach { rate ->
                    val resultForRate = result.resultByRate[rate]!!

                    Assertions.assertEquals(expectedWeightByRate[rate], resultForRate.targetWeight)
                    Assertions.assertEquals(expectedBmiByRate[rate], resultForRate.bmi)
                }
            }
        )
    }

    @Test
    @RunOnVertxContext
    fun `should calculate target weights for specific weight gain goal`(uniAsserter: UniAsserter) {
        val targetDate = LocalDate.now().plusDays(150) // suppose we set the target date 150 days later
        val rates = listOf(100, 200, 300, 400, 500)

        val expectedWeightByRate = mapOf(
            100 to 47f,
            200 to 49f,
            300 to 51f,
            400 to 53f,
            500 to 55f,
            600 to 57f,
            700 to 59f
        )

        val expectedBmiByRate = mapOf(
            100 to 20f,
            200 to 20f,
            300 to 21f,
            400 to 22f,
            500 to 23f,
            600 to 24f,
            700 to 25f
        )

        uniAsserter.assertThat(
            { wizard.calculateForTargetDate(WizardTargetDateInput(
                age = 30,
                height = 155,
                currentWeight = 45f,
                sex = CalculationSex.FEMALE,
                targetDate = targetDate,
                calculationGoal = CalculationGoal.GAIN)) },
            { result ->
                rates.forEach { rate ->
                    val resultForRate = result.resultByRate[rate]!!

                    Assertions.assertEquals(expectedWeightByRate[rate], resultForRate.targetWeight)
                    Assertions.assertEquals(expectedBmiByRate[rate], resultForRate.bmi)
                }
            }
        )
    }

    @Test
    @RunOnVertxContext
    fun `should filter non-recommendable results for weight loss goal`(uniAsserter: UniAsserter) {
        val targetDate = LocalDate.now().plusDays(300) // suppose we set the target date 300 days later

        uniAsserter.assertThat(
            { wizard.calculateForTargetDate(
                WizardTargetDateInput(
                    age = 30,
                    height = 170,
                    currentWeight = 60f,
                    sex = CalculationSex.MALE,
                    targetDate = targetDate,
                    calculationGoal = CalculationGoal.LOSS
                ))
            },
            { result ->
                result.resultByRate.values.forEach { ratedResult ->
                    Assertions.assertNotEquals(BmiCategory.UNDERWEIGHT, ratedResult.bmiCategory)
                }
            }
        )
    }

    @Test
    @RunOnVertxContext
    fun `should filter non-recommendable results for weight gain goal`(uniAsserter: UniAsserter) {
        val targetDate = LocalDate.now().plusDays(300) // suppose we set the target date 300 days later

        uniAsserter.assertThat(
            { wizard.calculateForTargetDate(WizardTargetDateInput(
                age = 30,
                height = 155,
                currentWeight = 45f,
                sex = CalculationSex.FEMALE,
                targetDate = targetDate,
                calculationGoal = CalculationGoal.GAIN
            )) },
            { result ->
                result.resultByRate.values.forEach { ratedResult ->
                    Assertions.assertTrue(ratedResult.bmiCategory != BmiCategory.OBESE && ratedResult.bmiCategory != BmiCategory.SEVERELY_OBESE)
                }
            }
        )
    }
}