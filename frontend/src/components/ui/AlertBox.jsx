const typeClass = {
  info: "alert-info",
  success: "alert-success",
  warning: "alert-warning",
  error: "alert-error"
};

export default function AlertBox({ type = "info", children }) {
  const cls = `alert-box ${typeClass[type] || typeClass.info}`;
  return <div className={cls}>{children}</div>;
}