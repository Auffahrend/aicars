package akostenko.aicars.drawing

import akostenko.aicars.math.Decart

import org.newdawn.slick.Color

abstract class Line

data class StraightLine(val from: Decart, val to: Decart, val color: Color, val width: Float) : Line()

data class ArcLine(val center: Decart, val radius: Double, val from: Double, val to: Double, val color: Color, val width: Float) : Line()

