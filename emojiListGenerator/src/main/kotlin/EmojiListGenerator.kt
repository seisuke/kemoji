import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.buildCodeBlock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.io.path.Path

object EmojiListGenerator {
    private val format = Json { isLenient = true }
    private const val JSON_FILE_NAME = "emoji.json"
    private const val PACKAGE_NAME = "kemoji"
    private const val CLASS_NAME = "Emoji"
    private const val PROPERTY_NAME = "emojis"
    private const val OBJECT_NAME = "EmojiList"

    fun fileLoader(): List<Emoji> {
        val json = this::class.java.getResource(JSON_FILE_NAME).readText(Charsets.UTF_8).trimIndent()
        return format.decodeFromString(json)
    }

    fun generateEmojiLists() {
        val listEmojiTypeName = List::class.asClassName().parameterizedBy(
            ClassName(PACKAGE_NAME, CLASS_NAME)
        )
        val funSpecs = createFunSpecs(listEmojiTypeName)
        val propertySpec = createPropertySpec(funSpecs, listEmojiTypeName)
        val emojiListObjectSpec = TypeSpec.objectBuilder(OBJECT_NAME)
            .addProperty(propertySpec)
            .apply {
                funSpecs.forEach {
                    addFunction(it)
                }
            }
            .build()
        val kotlinFile = FileSpec
            .builder(PACKAGE_NAME, OBJECT_NAME)
            .addType(emojiListObjectSpec)
            .indent("    ")
            .build()
        kotlinFile.writeTo(Path("../$PACKAGE_NAME/src/commonMain/kotlin"))
    }

    private fun createFunSpecs(listEmojiTypeName: ParameterizedTypeName): List<FunSpec> {
        val emojiListList = fileLoader().chunked(500) //for method size limit
        val functions = emojiListList.mapIndexed { index, emojiList ->
            FunSpec.builder("function${index + 1}")
                .returns(listEmojiTypeName)
                .addModifiers(KModifier.PRIVATE)
                .addCode(
                    buildCodeBlock {
                        add("return ")
                        listToCodeBlock(emojiList)
                    }
                ).build()
        }
        return functions
    }

    private fun createPropertySpec(
        funSpecs: List<FunSpec>,
        listEmojiTypeName: ParameterizedTypeName
    ): PropertySpec {
        val functionsString = funSpecs.joinToString(" + ") { function ->
            "${function.name}()"
        }
        val listCodeBlock = buildCodeBlock {
            add(functionsString)
        }
        return PropertySpec
            .builder(
                PROPERTY_NAME,
                listEmojiTypeName,
            )
            .initializer(listCodeBlock)
            .build()
    }

    private fun CodeBlock.Builder.listToCodeBlock(emojiList: List<Emoji>) {
        add("listOf(\n")
        indent()
        emojiList.forEach { emoji ->
            add(
                """|Emoji(
                   |    emoji = %S,
                   |    description = %S,
                   |    category = %S,
                   |    aliases = %L,
                   |    tags = %L,
                   |    unicodeVersion = %L,
                   |    iosVersion = %Lf,
                   |    supportsFitzpatrick = %L,
                   |),
                   |""".trimMargin(),
                emoji.emoji.replace(Char(65039).toString(), ""),
                emoji.description,
                emoji.category,
                emoji.aliases.toPoetString(),
                emoji.tags.toPoetString(),
                emoji.unicodeVersion.toPoetString(),
                emoji.iosVersion,
                emoji.supportsFitzpatrick,
            )
        }
        unindent()
        add(")")
    }

}

private fun List<String>.toPoetString(): String = if (this.isEmpty()) {
    "emptyList()"
} else {
    val listLiteral = this.joinToString { "\"$it\"" }
    "listOf($listLiteral)"
}




