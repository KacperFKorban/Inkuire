package org.virtuslab.inkuire.kotlin.model

data class SDRI(
    val packageName: String? = null,
    val className: String? = null,
    val callableName: String? = null,
    val original: String
) {
    override fun toString(): String = original
}

data class SDFunction(
    val dri: SDRI,
    val name: String,
    val isConstructor: Boolean,
    val parameters: List<SDParameter>,
    val areParametersDefault: List<Boolean>,
    val type: SBound,
    val generics: List<SDTypeParameter>,
    val receiver: SDParameter?,
    val packageName: String,
    val location: String
)

data class SDParameter(
    val dri: SDRI,
    val name: String?,
    val type: SBound
)

data class SDTypeParameter(
    val variantTypeParameter: SVariance<*>,
    val bounds: List<SBound>
)

data class AncestryGraph(
    val dri: SDRI,
    val type: SBound,
    val superTypes: List<SProjection>
)
