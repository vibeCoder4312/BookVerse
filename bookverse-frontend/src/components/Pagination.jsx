import "./Pagination.css";

// `page` is 0-indexed (matches Spring's Pageable convention), but we
// display it as 1-indexed since that's what people expect to read.
function Pagination({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) return null; // nothing to paginate - don't show controls at all

  const isFirstPage = page === 0;
  const isLastPage = page >= totalPages - 1;

  return (
    <div className="pagination">
      <button
        className="pagination__btn"
        onClick={() => onPageChange(page - 1)}
        disabled={isFirstPage}
      >
        ← Previous
      </button>

      <span className="pagination__status">
        Page {page + 1} of {totalPages}
      </span>

      <button
        className="pagination__btn"
        onClick={() => onPageChange(page + 1)}
        disabled={isLastPage}
      >
        Next →
      </button>
    </div>
  );
}

export default Pagination;
