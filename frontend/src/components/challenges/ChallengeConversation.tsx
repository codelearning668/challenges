import { useState, useEffect, useRef } from 'react'
import { Send, Trash2, CornerDownRight, X } from 'lucide-react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { challengeApi } from '@/services/api'
import { Button } from '@/components/shared/Button'
import { useConfirm } from '@/components/shared/ConfirmProvider'
import { useAuthStore } from '@/stores/useAuthStore'
import { usePermissions } from '@/hooks/usePermissions'
import { formatDateTime, formatDurationJson } from '@/utils/format'
import { toast } from '@/stores/useToastStore'
import type { ConversationEntryDetailResponse } from '@/types/challenge'

interface Props {
    challengeId: number
    conversation: ConversationEntryDetailResponse[]
    onClose: () => void
    canPostMessage: boolean
}

export function ChallengeConversation({
                                          challengeId,
                                          conversation,
                                          onClose,
                                          canPostMessage,
                                      }: Props) {
    const queryClient = useQueryClient()
    const confirm = useConfirm()
    const user = useAuthStore((s) => s.user)
    const { isAdmin } = usePermissions()

    const [newMessage, setNewMessage] = useState('')
    const [replyTo, setReplyTo] = useState<ConversationEntryDetailResponse | null>(null)
    const [highlightId, setHighlightId] = useState<number | null>(null)

    const messagesEndRef = useRef<HTMLDivElement>(null)

    useEffect(() => {
        if (messagesEndRef.current) {
            messagesEndRef.current.scrollTop = messagesEndRef.current.scrollHeight
        }
    }, [conversation.length])

    const invalidate = () =>
        queryClient.invalidateQueries({ queryKey: ['challenge', challengeId] })

    const postMutation = useMutation({
        mutationFn: (msg: string) => challengeApi.addConversationEntry(challengeId, msg),
        onSuccess: () => {
            invalidate()
            setNewMessage('')
            toast.success('Message posted')
        },
    })

    const replyMutation = useMutation({
        mutationFn: ({ parentId, msg }: { parentId: number; msg: string }) =>
            challengeApi.replyToConversationEntry(challengeId, parentId, msg),
        onSuccess: () => {
            invalidate()
            setNewMessage('')
            setReplyTo(null)
            toast.success('Reply posted')
        },
    })

    const deleteMutation = useMutation({
        mutationFn: (entryId: number) => challengeApi.deleteConversationEntry(entryId),
        onSuccess: () => {
            invalidate()
            toast.success('Message deleted')
        },
    })

    const handleDelete = async (entryId: number) => {
        const ok = await confirm({
            title: 'Delete message',
            message: 'Are you sure you want to delete this message?',
            confirmLabel: 'Delete',
            variant: 'danger',
        })
        if (ok) deleteMutation.mutate(entryId)
    }

    const handleSend = () => {
        const text = newMessage.trim()
        if (!text || !canPostMessage) return

        if (replyTo) {
            replyMutation.mutate({ parentId: replyTo.entryId, msg: text })
        } else {
            postMutation.mutate(text)
        }
    }

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter' && newMessage.trim() && canPostMessage) handleSend()
    }

    const scrollToMessage = (entryId: number) => {
        const el = document.getElementById(`msg-${entryId}`)
        if (el) {
            el.scrollIntoView({ behavior: 'smooth', block: 'center' })
            setHighlightId(entryId)
            setTimeout(() => setHighlightId(null), 2000)
        }
    }

    return (
        <div className="flex flex-col h-full bg-gray-50 dark:bg-gray-900/50 border-l border-gray-200 dark:border-gray-700">
            {/* Header */}
            <div className="flex items-center justify-between px-4 py-3 bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 shrink-0">
                <h2 className="text-sm font-semibold text-gray-900 dark:text-gray-100">
                    Challenge Chat
                </h2>
                <button
                    onClick={onClose}
                    className="p-1.5 rounded-lg text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
                    title="Close chat"
                >
                    <X className="w-4 h-4" />
                </button>
            </div>

            {/* Message Feed */}
            <div
                ref={messagesEndRef}
                className="flex-1 overflow-y-auto p-4 space-y-4"
            >
                {conversation.length === 0 ? (
                    <p className="text-center text-sm text-gray-500 dark:text-gray-400 mt-8">
                        No messages yet. Start the conversation!
                    </p>
                ) : (
                    conversation.map((msg) => {
                        const isMine = msg.participantName === user?.username
                        const canDelete = isAdmin
                        const parentMsg = msg.parentEntryId
                            ? conversation.find((c) => c.entryId === msg.parentEntryId)
                            : null

                        return (
                            <div
                                key={msg.entryId}
                                id={`msg-${msg.entryId}`}
                                className={`flex flex-col transition-all duration-500 rounded-2xl ${
                                    isMine ? 'items-end' : 'items-start'
                                } ${highlightId === msg.entryId ? 'ring-2 ring-primary-500 ring-offset-2 ring-offset-gray-50 dark:ring-offset-gray-900 bg-white/50 dark:bg-gray-800/50' : ''}`}
                            >
                                {/* Sender Info */}
                                <div className="flex items-center gap-2 mb-1 px-1">
                  <span className="text-sm font-semibold text-gray-700 dark:text-gray-300">
                    {isMine ? 'You' : msg.participantName}
                  </span>
                                    {msg.participantLapTimeAtPosting && (
                                        <span className="text-xs font-mono text-gray-500 bg-gray-200 dark:bg-gray-800 px-1.5 py-0.5 rounded">
                      {formatDurationJson(msg.participantLapTimeAtPosting)}
                    </span>
                                    )}
                                    <span className="text-xs text-gray-400">
                    {formatDateTime(msg.createdAt).split(' ').slice(-1)}
                  </span>
                                </div>

                                {/* Message Bubble */}
                                <div
                                    className={`group relative max-w-[85%] rounded-2xl px-4 py-2.5 text-sm shadow-sm ${
                                        isMine
                                            ? 'bg-primary-600 text-white rounded-tr-sm'
                                            : 'bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 rounded-tl-sm border border-gray-100 dark:border-gray-700'
                                    }`}
                                >
                                    {/* Parent snippet if it's a reply */}
                                    {parentMsg && (
                                        <div
                                            onClick={() => scrollToMessage(parentMsg.entryId)}
                                            className={`mb-2 p-2 rounded-lg text-xs border-l-2 truncate cursor-pointer hover:opacity-80 transition-opacity ${
                                                isMine
                                                    ? 'bg-primary-700/50 border-primary-400 text-primary-100'
                                                    : 'bg-gray-100 dark:bg-gray-900/50 border-gray-300 dark:border-gray-600 text-gray-500 dark:text-gray-400'
                                            }`}
                                            title="Click to jump to original message"
                                        >
                      <span className="font-semibold block text-[11px] mb-0.5">
                        {parentMsg.participantName}
                      </span>
                                            {parentMsg.message}
                                        </div>
                                    )}

                                    <p className="whitespace-pre-wrap break-words">{msg.message}</p>

                                    {/* Actions (Reply / Delete) on hover */}
                                    <div
                                        className={`absolute top-1/2 -translate-y-1/2 flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity ${
                                            isMine ? '-left-16' : '-right-16'
                                        }`}
                                    >
                                        {canPostMessage && (
                                            <button
                                                onClick={() => setReplyTo(msg)}
                                                className="p-1.5 rounded-full bg-gray-100 dark:bg-gray-700 text-gray-500 hover:text-primary-600 dark:hover:text-primary-400 shadow-sm"
                                                title="Reply"
                                            >
                                                <CornerDownRight className="w-3.5 h-3.5" />
                                            </button>
                                        )}
                                        {canDelete && (
                                            <button
                                                onClick={() => handleDelete(msg.entryId)}
                                                className="p-1.5 rounded-full bg-gray-100 dark:bg-gray-700 text-gray-500 hover:text-red-500 shadow-sm"
                                                title="Delete"
                                            >
                                                <Trash2 className="w-3.5 h-3.5" />
                                            </button>
                                        )}
                                    </div>
                                </div>
                            </div>
                        )
                    })
                )}
            </div>

            {/* Input Area */}
            <div className="p-3 bg-white dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700 shrink-0">
                {!canPostMessage ? (
                    <div className="text-center py-2 text-xs text-gray-500 dark:text-gray-400 bg-gray-50 dark:bg-gray-900/50 rounded-lg border border-gray-200 dark:border-gray-700">
                        Only registered participants can post messages in active challenges.
                    </div>
                ) : (
                    <>
                        {replyTo && (
                            <div className="flex items-center justify-between bg-gray-100 dark:bg-gray-900 rounded-t-lg px-3 py-2 text-xs border-l-2 border-primary-500 mb-2 text-gray-700 dark:text-gray-300">
                                <div className="truncate">
                                    Replying to <span className="font-semibold">{replyTo.participantName}</span>: {replyTo.message}
                                </div>
                                <button
                                    onClick={() => setReplyTo(null)}
                                    className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200"
                                >
                                    <X className="w-3.5 h-3.5" />
                                </button>
                            </div>
                        )}
                        <div className="flex gap-2 items-center">
                            <input
                                type="text"
                                placeholder="Type a message..."
                                value={newMessage}
                                onChange={(e) => setNewMessage(e.target.value)}
                                onKeyDown={handleKeyDown}
                                className="flex-1 px-4 py-2 rounded-full border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 text-sm text-gray-900 dark:text-gray-100 placeholder-gray-500 dark:placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500"
                                maxLength={1000}
                            />
                            <Button
                                onClick={handleSend}
                                disabled={!newMessage.trim()}
                                isLoading={postMutation.isPending || replyMutation.isPending}
                                className="rounded-full w-10 h-10 p-0 shrink-0"
                            >
                                <Send className="w-4 h-4" />
                            </Button>
                        </div>
                    </>
                )}
            </div>
        </div>
    )
}