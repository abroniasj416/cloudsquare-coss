export default function SectionTitle({ eyebrow, title, subtitle, actions }) {
  return (
    <div className="section-title">
      {eyebrow && <div className="eyebrow">{eyebrow}</div>}
      <div className="section-title-row">
        <div>
          <h2>{title}</h2>
          {subtitle && <p>{subtitle}</p>}
        </div>
        {actions && <div className="section-actions">{actions}</div>}
      </div>
    </div>
  );
}