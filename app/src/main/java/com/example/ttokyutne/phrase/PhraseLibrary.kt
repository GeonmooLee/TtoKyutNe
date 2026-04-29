package com.example.ttokyutne.phrase

object PhraseLibrary {
    val phrases = listOf(
        LocalPhrase(1001, PhraseCategory.VeryShort, "{interval} 만에 다시 켰어요. 지금 필요한 건 정보일까요, 안심일까요?"),
        LocalPhrase(1002, PhraseCategory.VeryShort, "{interval} 만에 손이 갔네요. 잠깐 마음을 살펴봐도 괜찮아요."),
        LocalPhrase(1003, PhraseCategory.VeryShort, "{interval} 만의 확인이에요. 혹시 기다리는 마음이 있었나요?"),
        LocalPhrase(1004, PhraseCategory.VeryShort, "{interval} 만에 다시 봤어요. 새 소식보다 안심이 필요했을지도 몰라요."),
        LocalPhrase(1005, PhraseCategory.VeryShort, "{interval} 만에 켰어요. 지금은 한숨 돌려도 괜찮아요."),
        LocalPhrase(1006, PhraseCategory.VeryShort, "{interval} 만의 재확인이에요. 마음이 조금 바빴을 수 있어요."),

        LocalPhrase(2001, PhraseCategory.ShortInterval, "{interval} 만에 다시 확인했어요. 지금 필요한 알림이 있었나요?"),
        LocalPhrase(2002, PhraseCategory.ShortInterval, "{interval} 만의 확인이에요. 잠깐 멈춰도 놓치는 건 적을 수 있어요."),
        LocalPhrase(2003, PhraseCategory.ShortInterval, "{interval} 만에 화면을 켰어요. 기다리는 마음을 알아차려봐요."),
        LocalPhrase(2004, PhraseCategory.ShortInterval, "{interval} 만에 돌아왔네요. 지금은 괜찮은지 스스로에게 물어봐요."),
        LocalPhrase(2005, PhraseCategory.ShortInterval, "{interval} 만의 재확인이에요. 확인보다 안심이 필요했을 수도 있어요."),
        LocalPhrase(2006, PhraseCategory.ShortInterval, "{interval} 만에 다시 켰어요. 중요한 건 잠시 뒤에 봐도 괜찮을 수 있어요."),

        LocalPhrase(3001, PhraseCategory.Night, "{interval} 만에 다시 켰어요. 오늘의 마지막 확인이어도 괜찮아요."),
        LocalPhrase(3002, PhraseCategory.Night, "{interval} 만의 밤 확인이에요. 내일 봐도 늦지 않을 수 있어요."),
        LocalPhrase(3003, PhraseCategory.Night, "{interval} 만에 화면이 켜졌어요. 지금은 쉬어도 되는 시간이에요."),
        LocalPhrase(3004, PhraseCategory.Night, "{interval} 만에 다시 봤어요. 잠들기 전 마음을 조금 내려놔요."),
        LocalPhrase(3005, PhraseCategory.Night, "{interval} 만의 확인이에요. 밤에는 알림보다 회복이 먼저일 수 있어요."),
        LocalPhrase(3006, PhraseCategory.Night, "{interval} 만에 켰어요. 오늘 온 연락은 내일의 나도 볼 수 있어요."),

        LocalPhrase(4001, PhraseCategory.FrequentDay, "{interval} 만에 다시 켰어요. 오늘은 폰을 자주 찾는 날일 수 있어요."),
        LocalPhrase(4002, PhraseCategory.FrequentDay, "{interval} 만의 확인이에요. 바쁜 마음이 화면으로 향했을지도 몰라요."),
        LocalPhrase(4003, PhraseCategory.FrequentDay, "{interval} 만에 돌아왔어요. 오늘의 반복을 알아차린 것만으로 충분해요."),
        LocalPhrase(4004, PhraseCategory.FrequentDay, "{interval} 만에 켰어요. 계속 확인하고 싶은 마음이 있었나요?"),
        LocalPhrase(4005, PhraseCategory.FrequentDay, "{interval} 만의 재확인이에요. 오늘은 잠깐 속도를 낮춰봐도 좋아요."),
        LocalPhrase(4006, PhraseCategory.FrequentDay, "{interval} 만에 다시 봤어요. 알림보다 마음의 긴장이 컸을 수 있어요."),

        LocalPhrase(5001, PhraseCategory.Focus, "{interval} 만에 다시 켰어요. 지금 필요한 건 새 정보보다 다시 집중하는 힘일지도 몰라요."),
        LocalPhrase(5002, PhraseCategory.Focus, "{interval} 만의 확인이에요. 하던 일로 천천히 돌아가도 괜찮아요."),
        LocalPhrase(5003, PhraseCategory.Focus, "{interval} 만에 화면을 봤어요. 한 번만 더 집중의 흐름을 이어가봐요."),
        LocalPhrase(5004, PhraseCategory.Focus, "{interval} 만에 다시 켰어요. 지금 하던 일에 마음을 돌려볼까요?"),
        LocalPhrase(5005, PhraseCategory.Focus, "{interval} 만의 재확인이에요. 필요한 알림은 조금 뒤에도 확인할 수 있어요."),
        LocalPhrase(5006, PhraseCategory.Focus, "{interval} 만에 돌아왔네요. 화면 밖의 흐름도 다시 이어갈 수 있어요.")
    )

    fun phrasesFor(category: PhraseCategory): List<LocalPhrase> {
        return phrases.filter { it.category == category }
    }

    fun formatIntervalSeconds(intervalSeconds: Long): String {
        return when {
            intervalSeconds < 60 -> "${intervalSeconds}초"
            else -> {
                val minutes = intervalSeconds / 60
                val seconds = intervalSeconds % 60
                if (seconds == 0L) "${minutes}분" else "${minutes}분 ${seconds}초"
            }
        }
    }
}
