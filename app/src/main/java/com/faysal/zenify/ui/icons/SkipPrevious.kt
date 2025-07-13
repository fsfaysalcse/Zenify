package com.faysal.zenify.ui.icons


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SkipPrevious: ImageVector
    get() {
        if (_TrackPrevious != null) return _TrackPrevious!!

        _TrackPrevious = ImageVector.Builder(
            name = "TrackPrevious",
            defaultWidth = 15.dp,
            defaultHeight = 15.dp,
            viewportWidth = 15f,
            viewportHeight = 15f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(1.94976f, 2.74989f)
                curveTo(1.94976f, 2.44613f, 2.196f, 2.19989f, 2.49976f, 2.19989f)
                curveTo(2.80351f, 2.19989f, 3.04976f, 2.44613f, 3.04976f, 2.74989f)
                verticalLineTo(7.2825f)
                curveTo(3.0954f, 7.18802f, 3.17046f, 7.10851f, 3.26662f, 7.05776f)
                lineTo(12.2666f, 2.30776f)
                curveTo(12.4216f, 2.22596f, 12.6081f, 2.23127f, 12.7582f, 2.32176f)
                curveTo(12.9083f, 2.41225f, 13f, 2.57471f, 13f, 2.74995f)
                verticalLineTo(12.25f)
                curveTo(13f, 12.4252f, 12.9083f, 12.5877f, 12.7582f, 12.6781f)
                curveTo(12.6081f, 12.7686f, 12.4216f, 12.7739f, 12.2666f, 12.6921f)
                lineTo(3.26662f, 7.94214f)
                curveTo(3.17046f, 7.89139f, 3.0954f, 7.81188f, 3.04976f, 7.7174f)
                verticalLineTo(12.2499f)
                curveTo(3.04976f, 12.5536f, 2.80351f, 12.7999f, 2.49976f, 12.7999f)
                curveTo(2.196f, 12.7999f, 1.94976f, 12.5536f, 1.94976f, 12.2499f)
                verticalLineTo(2.74989f)
                close()
                moveTo(4.57122f, 7.49995f)
                lineTo(12f, 11.4207f)
                verticalLineTo(3.5792f)
                lineTo(4.57122f, 7.49995f)
                close()
            }
        }.build()

        return _TrackPrevious!!
    }

private var _TrackPrevious: ImageVector? = null

