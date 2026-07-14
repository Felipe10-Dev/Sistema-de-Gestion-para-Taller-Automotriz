const DEV = import.meta.env.DEV

export function initSecurity() {
    protectConsole()
}

function protectConsole() {
    if (DEV) return

    const noop = () => undefined
    const methods = ['log', 'warn', 'info', 'debug', 'trace'] as const
    for (const method of methods) {
        const original = (console as any)[method]
        ;(console as any)[method] = (...args: any[]) => {
            const str = args.join(' ')
            if (str.includes('token') || str.includes('jwt') ||
                str.includes('password') || str.includes('secret') ||
                str.includes('Authorization')) {
                return
            }
            original.apply(console, args)
        }
    }

    Object.defineProperty(window, 'console', {
        value: console,
        writable: false,
        configurable: false
    })
}

export function sanitize(input: string): string {
    const map: Record<string, string> = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#x27;',
        '/': '&#x2F;'
    }
    const reg = /[&<>"'/]/g
    return input.replace(reg, (match) => map[match])
}

export function isValidToken(token: string): boolean {
    if (!token || typeof token !== 'string') return false
    const parts = token.split('.')
    if (parts.length !== 3) return false
    try {
        const payload = JSON.parse(atob(parts[1]))
        if (!payload.exp) return false
        return payload.exp * 1000 > Date.now()
    } catch {
        return false
    }
}

export function safeJsonParse<T>(text: string, fallback: T): T {
    try {
        return JSON.parse(text) as T
    } catch {
        return fallback
    }
}
