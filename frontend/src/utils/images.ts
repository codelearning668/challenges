const IMAGE_ENABLED_SIMULATORS = new Set<string>(['assetto-corsa'])

const IMAGE_BASE = `${import.meta.env.VITE_API_URL || '/api'}/images`

function stripDiacritics(value: string): string {
    return value.normalize('NFD').replace(/[\u0300-\u036f]/g, '')
}

function alnumSlug(value: string | null | undefined): string | null {
    if (!value) return null
    const slug = stripDiacritics(value)
        .toLowerCase()
        .replace(/[^a-z0-9]+/g, '-')
        .replace(/^-|-$/g, '')
    return slug || null
}

function trackSlug(value: string | null | undefined): string | null {
    if (!value) return null
    const slug = stripDiacritics(value).toLowerCase().replace(/ /g, '-')
    return slug || null
}

export function getCarImageUrl(
    name: string | null | undefined,
    simulatorName: string | null | undefined,
): string | undefined {
    const sim = alnumSlug(simulatorName)
    if (!sim || !IMAGE_ENABLED_SIMULATORS.has(sim)) return undefined
    const slug = alnumSlug(name)
    if (!slug) return undefined
    return `${IMAGE_BASE}/${sim}/cars/${slug}.jpg`
}

export function getTrackImageUrl(
    name: string | null | undefined,
    simulatorName: string | null | undefined,
): string | undefined {
    const sim = alnumSlug(simulatorName)
    if (!sim || !IMAGE_ENABLED_SIMULATORS.has(sim)) return undefined
    const slug = trackSlug(name)
    if (!slug) return undefined
    return `${IMAGE_BASE}/${sim}/tracks/${slug}.png`
}