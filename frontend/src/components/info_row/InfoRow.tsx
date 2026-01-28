export function InfoRow({ label, value }: { label: string; value: string }) {
    return (
        <div
            style={{
                display: "flex",
                justifyContent: "space-between",
                padding: "4px 0",
                fontSize: "0.9em",
            }}
        >
            <span style={{ color: "#666" }}>{label}</span>
            <strong>{value}</strong>
        </div>
    );
}