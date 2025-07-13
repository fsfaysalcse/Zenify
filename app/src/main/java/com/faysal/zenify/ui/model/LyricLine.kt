package com.faysal.zenify.ui.model


data class LyricLine(
    val text: String,
    val startTime: Long, // in milliseconds
    val endTime: Long
)

data class Song(
    val title: String,
    val artist: String,
    val lyrics: List<LyricLine>
)

val BlindingLights = Song(
    title = "Blinding Lights",
    artist = "The Weeknd",
    lyrics = listOf(
        LyricLine("I've been tryna call", 2000, 4500),
        LyricLine("I've been on my own for long enough", 4500, 8000),
        LyricLine("Maybe you can show me how to love, maybe", 8000, 12000),
        LyricLine("I feel like I'm just missing something when you're gone", 12000, 16500),
        LyricLine("But I've been feeling numb without you", 16500, 20000),
        LyricLine("I've been doing drugs just to feel like I'm alive", 20000, 24500),
        LyricLine("I've been thinking 'bout you all the time", 24500, 28000),
        LyricLine("I can't sleep until I feel your touch", 28000, 32000),
        LyricLine("I said, ooh, I'm blinded by the lights", 32000, 36000),
        LyricLine("No, I can't sleep until I feel your touch", 36000, 40000),
        LyricLine("I said, ooh, I'm blinded by the lights", 40000, 44000),
        LyricLine("No, I can't sleep until I feel your touch", 44000, 48000),
        LyricLine("I'm running out of time", 48000, 51000),
        LyricLine("'Cause I can see the sun light up the sky", 51000, 55000),
        LyricLine("So I hit the road in overdrive, baby, oh", 55000, 59000),
        LyricLine("The city's cold and empty (Oh)", 59000, 62500),
        LyricLine("No one's around to judge me (Oh)", 62500, 66000),
        LyricLine("I can't see clearly when you're gone", 66000, 70000),
        LyricLine("I said, ooh, I'm blinded by the lights", 70000, 74000),
        LyricLine("No, I can't sleep until I feel your touch", 74000, 78000),
        LyricLine("I said, ooh, I'm blinded by the lights", 78000, 82000),
        LyricLine("No, I can't sleep until I feel your touch", 82000, 86000)
    )
)
