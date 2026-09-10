import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

interface TableProps<T> {
  columns: Array<{
    key: keyof T | string
    label: string
    render?: (value: any, row: T) => React.ReactNode
  }>
  rows: T[]
  onRowClick?: (row: T) => void
  emptyMessage?: string
  className?: string
}

export function Table<T>({
  columns,
  rows,
  onRowClick,
  emptyMessage = 'No data available',
  className,
}: TableProps<T>) {
  if (rows.length === 0) {
    return (
      <div className={cn('text-center py-8 text-gray-500 dark:text-gray-400', className)}>
        {emptyMessage}
      </div>
    )
  }

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
        <thead className="bg-gray-50 dark:bg-gray-800">
          <tr>
            {columns.map((column) => (
              <th
                key={String(column.key)}
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider"
              >
                {column.label}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
          {rows.map((row, index) => (
            <tr
              key={index}
              onClick={() => onRowClick?.(row)}
              className={onRowClick ? 'cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-700' : ''}
            >
              {columns.map((column) => (
                <td key={String(column.key)} className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100">
                  {column.render ? column.render(row[column.key as keyof typeof row], row) : String(row[column.key as keyof typeof row])}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
