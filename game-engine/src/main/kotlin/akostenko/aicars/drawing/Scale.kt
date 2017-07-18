package akostenko.aicars.drawing

import akostenko.math.vector.Vector

data class Scale(val size: Double, val pixels: Float) {
    fun to(value: Double) : Float = value.toFloat() / size.toFloat() * pixels
    fun to(vector: Vector) : Vector = vector / size * pixels.toDouble()
    fun from(pixels: Float) : Double = pixels / this.pixels * size

    companion object {
        private val splitter = ":"
        fun serialize(scale: Scale) : String = "${scale.size}$splitter${scale.pixels}"
        fun deserialize(s : String) : Scale {
            val tokens = s.split(splitter.toRegex())
            return Scale(tokens[0].toDouble(), tokens[1].toFloat())
        }
    }
}
