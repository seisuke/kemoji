package io.github.seisuke.emojilistgenerator

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
import io.github.seisuke.kemoji.Generated
import kotlin.io.path.Path

object EmojiListGenerator {
    private const val JSON_FILE_NAME = "/emoji.json"
    private const val PACKAGE_NAME = "io.github.seisuke.kemoji"
    private const val CLASS_NAME = "Emoji"
    private const val OBJECT_NAME = "EmojiList"
    private const val PROPERTY_NAME = "emojis"
    private val format = Json { isLenient = true }

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
            .addAnnotation(Generated::class)
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

        kotlinFile.writeTo(Path("../kemoji/src/commonMain/kotlin/"))
    }

    private fun createFunSpecs(listEmojiTypeName: ParameterizedTypeName): List<FunSpec> {
        val emojiList = fileLoader()
        val emojiTestList = EmojiTestReader.loadEmojiTest()
        val emojiGroupList = EmojiTestReader.groupByRawUnicode(emojiTestList)

        val functions = emojiList.chunked(500).mapIndexed { index, chunkedEmojiList ->
            FunSpec.builder("function${index + 1}")
                .returns(listEmojiTypeName)
                .addModifiers(KModifier.PRIVATE)
                .addCode(
                    buildCodeBlock {
                        add("return ")
                        listToCodeBlock(chunkedEmojiList, emojiGroupList)
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

    private fun CodeBlock.Builder.listToCodeBlock(
        emojiList: List<Emoji>,
        emojiGroupList: Map<String, List<EmojiTestReader.EmojiTest>>
    ) {
        add("listOf(\n")
        indent()
        emojiList.forEach { emoji ->
            val minimallyUnicode = emoji.emoji.replace(VARIATION_SELECTOR_16, "")
            val group = emojiGroupList[minimallyUnicode]
            val fitzpatrickIndex = if (group != null) {
                group.maxBy { it.emoji.length }.emoji.indexesOf(fitzpatrickRegex)
            } else {
                emptyList()
            }

            val vs16Index = if (emoji.emoji.contains(VARIATION_SELECTOR_16)) {
                emoji.emoji.indexesOf(VARIATION_SELECTOR_16.toRegex())
            } else {
                emptyList()
            }

            add(
                """|Emoji(
                   |    emoji = %S,
                   |    description = %S,
                   |    category = %S,
                   |    aliases = %L,
                   |    tags = %L,
                   |    unicodeVersion = %L,
                   |    iosVersion = %Lf,
                   |    fitzpatrickIndex = %L,
                   |    vs16Index = %L,
                   |),
                   |""".trimMargin(),
                minimallyUnicode,
                emoji.description,
                emoji.category,
                emoji.aliases.toPoetString(),
                emoji.tags.toPoetString(),
                emoji.unicodeVersion.toPoetString(),
                emoji.iosVersion,
                fitzpatrickIndex.toPoetString(),
                vs16Index.toPoetString(),
            )
        }
        unindent()
        add(")")
    }

}

fun String.indexesOf(regex: Regex): List<Int> = regex
    .findAll(this)
    .map { it.range.first }
    .toList()

private fun List<String>.toPoetString(): String = if (this.isEmpty()) {
    "emptyList()"
} else {
    val listLiteral = this.joinToString { "\"$it\"" }
    "listOf($listLiteral)"
}

@JvmName("toPoetStringInt")
private fun List<Int>.toPoetString(): String = if (this.isEmpty()) {
    "emptyList()"
} else {
    val listLiteral = this.joinToString { it.toString() }
    "listOf($listLiteral)"
}


