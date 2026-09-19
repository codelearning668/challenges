import { Trophy } from 'lucide-react'

interface RankBadgeProps {
    index: number
}

const MEDALS = [
    { label: '1st', color: 'text-yellow-500', text: 'text-yellow-600 dark:text-yellow-400' },
    { label: '2nd', color: 'text-gray-400',   text: 'text-gray-500 dark:text-gray-400' },
    { label: '3rd', color: 'text-amber-700',  text: 'text-amber-700 dark:text-amber-500' },
] as const

const EMOJI_RANKS: Record<number, { emoji: string; label: string; text: string }> = {
    3: { emoji: '💩',   label: '4th', text: 'text-amber-900 dark:text-amber-700' },
    4: { emoji: '♿', label: '5th', text: 'text-pink-600 dark:text-pink-400' },
}

export function RankBadge({ index }: RankBadgeProps) {
    const medal = MEDALS[index]
    if (medal) {
        return (
            <span className="inline-flex items-center gap-1 text-xs font-semibold">
        <Trophy className={`w-4 h-4 ${medal.color}`} />
        <span className={medal.text}>{medal.label}</span>
      </span>
        )
    }

    const emojiRank = EMOJI_RANKS[index]
    if (emojiRank) {
        return (
            <span className="inline-flex items-center gap-1 text-xs font-semibold">
        <span className="text-base leading-none" aria-hidden>
          {emojiRank.emoji}
        </span>
        <span className={emojiRank.text}>{emojiRank.label}</span>
      </span>
        )
    }

    return (
        <span className="text-xs text-gray-400 dark:text-gray-500 tabular-nums">
      {index + 1}.
    </span>
    )
}