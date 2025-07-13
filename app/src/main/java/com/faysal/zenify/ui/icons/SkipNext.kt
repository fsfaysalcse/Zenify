package com.faysal.zenify.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SkipNext: ImageVector
    get() {
        if (_TrackNext != null) return _TrackNext!!

        _TrackNext = ImageVector.Builder(
            name = "TrackNext",
            defaultWidth = 15.dp,
            defaultHeight = 15.dp,
            viewportWidth = 15f,
            viewportHeight = 15f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(13.0502f, 2.74989f)
                curveTo(13.0502f, 2.44613f, 12.804f, 2.19989f, 12.5002f, 2.19989f)
                curveTo(12.1965f, 2.19989f, 11.9502f, 2.44613f, 11.9502f, 2.74989f)
                verticalLineTo(7.2825f)
                curveTo(11.9046f, 7.18802f, 11.8295f, 7.10851f, 11.7334f, 7.05776f)
                lineTo(2.73338f, 2.30776f)
                curveTo(2.5784f, 2.22596f, 2.3919f, 2.23127f, 2.24182f, 2.32176f)
                curveTo(2.09175f, 2.41225f, 2f, 2.57471f, 2f, 2.74995f)
                verticalLineTo(12.25f)
                curveTo(2f, 12.4252f, 2.09175f, 12.5877f, 2.24182f, 12.6781f)
                curveTo(2.3919f, 12.7686f, 2.5784f, 12.7739f, 2.73338f, 12.6921f)
                lineTo(11.7334f, 7.94214f)
                curveTo(11.8295f, 7.89139f, 11.9046f, 7.81188f, 11.9502f, 7.7174f)
                verticalLineTo(12.2499f)
                curveTo(11.9502f, 12.5536f, 12.1965f, 12.7999f, 12.5002f, 12.7999f)
                curveTo(12.804f, 12.7999f, 13.0502f, 12.5536f, 13.0502f, 12.2499f)
                verticalLineTo(2.74989f)
                close()
                moveTo(3f, 11.4207f)
                verticalLineTo(3.5792f)
                lineTo(10.4288f, 7.49995f)
                lineTo(3f, 11.4207f)
                close()
            }
        }.build()

        return _TrackNext!!
    }

private var _TrackNext: ImageVector? = null

