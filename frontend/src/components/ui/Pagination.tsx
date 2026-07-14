interface PaginationProps {
  page: number
  totalPages: number
  totalElements: number
  onPageChange: (page: number) => void
}

export default function Pagination({ page, totalPages, totalElements, onPageChange }: PaginationProps) {
  if (totalPages <= 1) return null

  const getPages = () => {
    const pages: number[] = []
    const start = Math.max(0, page - 2)
    const end = Math.min(totalPages - 1, page + 2)
    if (start > 0) pages.push(0)
    if (start > 1) pages.push(-1)
    for (let i = start; i <= end; i++) pages.push(i)
    if (end < totalPages - 2) pages.push(-2)
    if (end < totalPages - 1) pages.push(totalPages - 1)
    return pages
  }

  return (
    <div className="flex items-center justify-between px-4 py-2 bg-white border-t border-gray-100">
      <span className="text-xs text-gray-400">{totalElements} registro{totalElements !== 1 ? 's' : ''}</span>
      <div className="flex items-center gap-0.5">
        <button
          onClick={() => onPageChange(page - 1)}
          disabled={page === 0}
          className="w-7 h-7 flex items-center justify-center text-sm text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
        >
          ‹
        </button>
        <span className="text-xs text-gray-400 mx-1">{page + 1} / {totalPages}</span>
        <button
          onClick={() => onPageChange(page + 1)}
          disabled={page >= totalPages - 1}
          className="w-7 h-7 flex items-center justify-center text-sm text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
        >
          ›
        </button>
      </div>
    </div>
  )
}
