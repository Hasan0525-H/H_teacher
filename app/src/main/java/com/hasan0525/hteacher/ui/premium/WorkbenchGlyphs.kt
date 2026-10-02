package com.hasan0525.hteacher.ui.premium

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * H/Line: original consistent 24-unit stroke icons with rounded endpoints.
 * No Material Icons or system Unicode glyph substitutes.
 * Forward/back arrows intentionally mirror for Arabic RTL.
 */
enum class EduGlyph {
    DASH, BOOK, EXAM, FOLDER, GROUP, DATE, CHART,
    PDF, PLUS, ARROW, SEARCH, SPARK, CHECK, DOC, MORE, BACK
}

@Composable
fun Glyph(name: EduGlyph, modifier: Modifier = Modifier, tint: Color = Edu.Teal) {
    Canvas(modifier.size(25.dp)) {
        val u = size.minDimension / 24f
        val w = 1.75f * u
        fun pt(x: Float, y: Float) = Offset(x * u, y * u)
        fun seg(ax: Float, ay: Float, bx: Float, by: Float, color: Color = tint) {
            drawLine(color, pt(ax, ay), pt(bx, by), strokeWidth = w, cap = StrokeCap.Round)
        }
        fun outline(x: Float, y: Float, dx: Float, dy: Float, rad: Float = 2.5f) {
            drawRoundRect(
                tint, topLeft = pt(x, y), size = Size(dx * u, dy * u),
                cornerRadius = CornerRadius(rad * u), style = Stroke(width = w)
            )
        }
        fun plate(x: Float, y: Float, dx: Float, dy: Float, rad: Float = 2f, alpha: Float = .14f) {
            drawRoundRect(tint.copy(alpha = alpha), topLeft = pt(x, y),
                size = Size(dx * u, dy * u), cornerRadius = CornerRadius(rad * u))
        }
        fun circle(x: Float, y: Float, r: Float) {
            drawCircle(tint, r * u, pt(x, y), style = Stroke(width = w))
        }
        when (name) {
            EduGlyph.DASH -> {
                plate(3f, 3f, 8f, 9f); outline(3f, 3f, 8f, 9f)
                plate(14f, 3f, 7f, 5f); outline(14f, 3f, 7f, 5f)
                outline(3f, 15f, 8f, 6f); plate(14f, 11f, 7f, 10f)
                outline(14f, 11f, 7f, 10f)
            }
            EduGlyph.BOOK -> {
                val left = Path().apply {
                    moveTo(12f*u, 7f*u); quadraticBezierTo(8f*u, 4f*u, 3f*u, 5f*u)
                    lineTo(3f*u, 19f*u); quadraticBezierTo(8f*u, 18f*u, 12f*u, 21f*u)
                }
                val right = Path().apply {
                    moveTo(12f*u, 7f*u); quadraticBezierTo(16f*u, 4f*u, 21f*u, 5f*u)
                    lineTo(21f*u, 19f*u); quadraticBezierTo(16f*u, 18f*u, 12f*u, 21f*u)
                }
                drawPath(left, tint, style=Stroke(width=w, cap=StrokeCap.Round))
                drawPath(right, tint, style=Stroke(width=w, cap=StrokeCap.Round))
                seg(12f, 7f, 12f, 21f); seg(6f, 10f, 9f, 11f); seg(15f, 11f, 18f, 10f)
            }
            EduGlyph.EXAM -> {
                plate(5f, 5f, 14f, 16f); outline(5f, 5f, 14f, 16f)
                outline(9f, 3f, 6f, 4f, 1.3f)
                seg(8f, 11f, 9.5f, 12.5f); seg(9.5f, 12.5f, 12f, 9.5f)
                seg(14f, 11f, 16.5f, 11f); seg(8f, 17f, 16f, 17f)
            }
            EduGlyph.FOLDER -> {
                plate(3f, 7f, 18f, 13f); outline(3f, 7f, 18f, 13f)
                seg(4f, 7f, 6.5f, 4.5f); seg(6.5f, 4.5f, 11f, 4.5f)
                seg(11f, 4.5f, 13f, 7f)
            }
            EduGlyph.GROUP -> {
                circle(9f, 8f, 3f); circle(18f, 9f, 2f)
                seg(3.5f, 19f, 3.5f, 17f); seg(3.5f, 17f, 6.5f, 14f)
                seg(6.5f, 14f, 11.5f, 14f); seg(11.5f, 14f, 14.5f, 17f)
                seg(14.5f, 17f, 14.5f, 19f); seg(3.5f, 19f, 14.5f, 19f)
                seg(17f, 15f, 20f, 16.5f); seg(20f, 16.5f, 21f, 20f)
            }
            EduGlyph.DATE -> {
                plate(3f, 5f, 18f, 16f); outline(3f, 5f, 18f, 16f)
                seg(3f, 10f, 21f, 10f); seg(8f, 3f, 8f, 7f); seg(16f, 3f, 16f, 7f)
                seg(8f, 15f, 11f, 18f); seg(11f, 18f, 17f, 13f)
            }
            EduGlyph.CHART -> {
                seg(3f, 4f, 3f, 20f); seg(3f, 20f, 21f, 20f)
                plate(6f, 13f, 3f, 6f); plate(11f, 10f, 3f, 9f); plate(16f, 5f, 3f, 14f)
                seg(6f, 13f, 10.5f, 9f); seg(10.5f, 9f, 14f, 11f); seg(14f, 11f, 20f, 4f)
            }
            EduGlyph.PDF, EduGlyph.DOC -> {
                plate(5f, 3f, 14f, 18f); outline(5f, 3f, 14f, 18f)
                seg(13f, 3f, 13f, 8f); seg(13f, 8f, 19f, 8f)
                if(name == EduGlyph.PDF) {
                    seg(8f, 12f, 16f, 12f); seg(8f, 15f, 16f, 15f)
                    seg(8f, 18f, 13f, 18f)
                } else {
                    seg(8f, 12f, 16f, 12f); seg(8f, 16f, 13f, 16f)
                }
            }
            EduGlyph.PLUS -> {
                plate(4f, 4f, 16f, 16f, 6f)
                seg(12f, 7f, 12f, 17f); seg(7f, 12f, 17f, 12f)
            }
            EduGlyph.ARROW -> { seg(20f, 12f, 4f, 12f); seg(10f, 6f, 4f, 12f); seg(4f, 12f, 10f, 18f) }
            EduGlyph.BACK -> { seg(4f, 12f, 20f, 12f); seg(14f, 6f, 20f, 12f); seg(20f, 12f, 14f, 18f) }
            EduGlyph.SEARCH -> { circle(10.5f, 10.5f, 6f); seg(15.2f, 15.2f, 21f, 21f) }
            EduGlyph.SPARK -> {
                val star = Path().apply {
                    moveTo(12f*u, 2f*u); lineTo(14.6f*u, 9.4f*u)
                    lineTo(22f*u, 12f*u); lineTo(14.6f*u, 14.6f*u)
                    lineTo(12f*u, 22f*u); lineTo(9.4f*u, 14.6f*u)
                    lineTo(2f*u, 12f*u); lineTo(9.4f*u, 9.4f*u); close()
                }
                drawPath(star, tint.copy(alpha=.12f))
                drawPath(star, tint, style=Stroke(width=w, cap=StrokeCap.Round))
            }
            EduGlyph.CHECK -> { seg(3f, 12f, 9f, 18f); seg(9f, 18f, 21f, 5f) }
            EduGlyph.MORE -> { circle(5f, 12f, 1f); circle(12f, 12f, 1f); circle(19f, 12f, 1f) }
        }
    }
}
