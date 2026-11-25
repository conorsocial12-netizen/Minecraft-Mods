import React from "react";
import { BANG, COLORS, HAND, INK } from "./kit";

const line = { stroke: INK, strokeWidth: 6, strokeLinejoin: "round" as const, strokeLinecap: "round" as const };

/** Scales a group around an anchor point (used for pop-in animations). */
export const PopG: React.FC<{ x: number; y: number; s: number; children: React.ReactNode }> = ({ x, y, s, children }) =>
  s <= 0.001 ? null : <g transform={`translate(${x} ${y}) scale(${s}) translate(${-x} ${-y})`}>{children}</g>;

export const Ground: React.FC<{ y: number; color?: string }> = ({ y, color = "#efe4cf" }) => (
  <g>
    <rect x={0} y={y} width={1920} height={1080 - y} fill={color} />
    <path d={`M0 ${y} L1920 ${y}`} {...line} />
  </g>
);

export const Frame: React.FC<{ x: number; y: number; w: number; h: number; children?: React.ReactNode; fill?: string }> = ({
  x,
  y,
  w,
  h,
  children,
  fill = "#fff",
}) => (
  <g>
    <rect x={x} y={y} width={w} height={h} rx={6} fill="#c98b52" {...line} />
    <rect x={x + 14} y={y + 14} width={w - 28} height={h - 28} fill={fill} {...line} strokeWidth={4} />
    {children}
  </g>
);

export const Plant: React.FC<{ x: number; y: number }> = ({ x, y }) => (
  <g>
    <path d={`M${x} ${y - 90} Q${x - 70} ${y - 170} ${x - 40} ${y - 220} M${x} ${y - 90} Q${x + 10} ${y - 200} ${x + 50} ${y - 240} M${x} ${y - 90} Q${x + 80} ${y - 140} ${x + 100} ${y - 170}`} fill="none" stroke="#3a9a52" strokeWidth={10} strokeLinecap="round" />
    <path d={`M${x - 55} ${y - 95} L${x + 55} ${y - 95} L${x + 40} ${y} L${x - 40} ${y} Z`} fill="#d9774a" {...line} />
  </g>
);

export const Window: React.FC<{ x: number; y: number; w?: number; h?: number; sky?: string }> = ({ x, y, w = 300, h = 240, sky = "#bfe3ff" }) => (
  <g>
    <rect x={x} y={y} width={w} height={h} fill={sky} {...line} />
    <path d={`M${x + w / 2} ${y} L${x + w / 2} ${y + h} M${x} ${y + h / 2} L${x + w} ${y + h / 2}`} {...line} />
  </g>
);

export const Table: React.FC<{ x1: number; x2: number; y: number; cloth?: string }> = ({ x1, x2, y, cloth = "#f7d9a6" }) => (
  <g>
    <rect x={x1 + 40} y={y} width={16} height={1080} fill={INK} />
    <rect x={x2 - 56} y={y} width={16} height={1080} fill={INK} />
    <path d={`M${x1} ${y} L${x2} ${y} L${x2 + 10} ${y + 120} L${x1 - 10} ${y + 120} Z`} fill={cloth} {...line} />
    <path d={`M${x1 + 40} ${y + 120} q30 25 60 0 q30 25 60 0`} fill="none" stroke="none" />
  </g>
);

export const Flame: React.FC<{ x: number; y: number; s?: number; t: number }> = ({ x, y, s = 1, t }) => {
  const f = 1 + Math.sin(t * 0.8 + x) * 0.12;
  return (
    <g transform={`translate(${x} ${y}) scale(${s} ${s * f})`}>
      <path d="M0 0 C-40 -10 -40 -60 -10 -90 C-10 -60 10 -60 5 -110 C40 -80 45 -30 30 -10 C25 0 10 2 0 0 Z" fill="#ff7a2f" {...line} strokeWidth={4} />
      <path d="M2 -8 C-15 -15 -15 -40 0 -55 C5 -35 20 -35 15 -15 C12 -8 8 -6 2 -8 Z" fill="#ffd23f" />
    </g>
  );
};

export const Pepper: React.FC<{ x: number; y: number; r?: number }> = ({ x, y, r = 0 }) => (
  <g transform={`translate(${x} ${y}) rotate(${r})`}>
    <path d="M-40 0 C-30 -25 20 -25 45 5 C20 -2 -20 10 -40 0 Z" fill="#e5483b" {...line} strokeWidth={4} />
    <path d="M-40 0 L-55 -12" stroke="#3a9a52" strokeWidth={7} strokeLinecap="round" />
  </g>
);

export const Plate: React.FC<{ x: number; y: number; children?: React.ReactNode }> = ({ x, y, children }) => (
  <g>
    <ellipse cx={x} cy={y} rx={85} ry={22} fill="white" {...line} strokeWidth={5} />
    {children}
  </g>
);

export const Ketchup: React.FC<{ x: number; y: number; s?: number }> = ({ x, y, s = 1 }) => (
  <g transform={`translate(${x} ${y}) scale(${s})`}>
    <path d="M-30 0 L-35 -110 Q-35 -140 -12 -150 L-10 -180 L10 -180 L12 -150 Q35 -140 35 -110 L30 0 Z" fill="#e5483b" {...line} strokeWidth={5} />
    <rect x={-14} y={-200} width={28} height={22} rx={4} fill="white" {...line} strokeWidth={4} />
    <rect x={-26} y={-95} width={52} height={40} rx={6} fill="white" {...line} strokeWidth={3} />
    <text x={0} y={-67} textAnchor="middle" fontFamily={BANG} fontSize={22} fill={INK}>
      MILD
    </text>
  </g>
);

export const Turkey: React.FC<{ x: number; y: number; legless?: boolean }> = ({ x, y, legless = false }) => (
  <g>
    <ellipse cx={x} cy={y + 8} rx={150} ry={26} fill="white" {...line} strokeWidth={5} />
    <path d={`M${x - 110} ${y} Q${x - 120} ${y - 120} ${x} ${y - 125} Q${x + 120} ${y - 120} ${x + 110} ${y} Z`} fill="#c8813f" {...line} />
    <path d={`M${x - 50} ${y - 85} Q${x} ${y - 105} ${x + 40} ${y - 80}`} fill="none" stroke="#f3c98c" strokeWidth={10} strokeLinecap="round" />
    {legless ? null : (
      <>
        <Drumstick x={x - 95} y={y - 70} r={-30} />
        <Drumstick x={x + 95} y={y - 70} r={30} />
      </>
    )}
    {legless ? <Drumstick x={x - 95} y={y - 70} r={-30} /> : null}
  </g>
);

export const Drumstick: React.FC<{ x: number; y: number; r?: number; s?: number }> = ({ x, y, r = 0, s = 1 }) => (
  <g transform={`translate(${x} ${y}) rotate(${r}) scale(${s})`}>
    <ellipse cx={0} cy={0} rx={30} ry={42} fill="#b86f30" {...line} strokeWidth={5} />
    <path d="M0 40 L0 80" stroke="white" strokeWidth={14} strokeLinecap="round" />
    <path d="M0 40 L0 80" stroke={INK} strokeWidth={4} strokeLinecap="round" opacity={0.0} />
    <circle cx={-8} cy={86} r={9} fill="white" {...line} strokeWidth={4} />
    <circle cx={8} cy={86} r={9} fill="white" {...line} strokeWidth={4} />
  </g>
);

export const Gift: React.FC<{ x: number; y: number; s?: number; label?: string }> = ({ x, y, s = 1, label = "DNA" }) => (
  <g transform={`translate(${x} ${y}) scale(${s})`}>
    <rect x={-70} y={-110} width={140} height={110} fill="#5ec8c0" {...line} />
    <rect x={-80} y={-135} width={160} height={30} fill="#5ec8c0" {...line} />
    <rect x={-12} y={-135} width={24} height={135} fill={COLORS.red} {...line} strokeWidth={4} />
    <path d="M0 -135 Q-50 -190 -40 -140 M0 -135 Q50 -190 40 -140" fill="none" stroke={COLORS.red} strokeWidth={10} strokeLinecap="round" />
    <text x={-40} y={-35} textAnchor="middle" fontFamily={BANG} fontSize={34} fill={INK}>
      {label}
    </text>
  </g>
);

export const Tube: React.FC<{ x: number; y: number; r?: number; s?: number }> = ({ x, y, r = 0, s = 1 }) => (
  <g transform={`translate(${x} ${y}) rotate(${r}) scale(${s})`}>
    <rect x={-16} y={-90} width={32} height={90} rx={14} fill="#e8f6ff" {...line} strokeWidth={5} />
    <rect x={-20} y={-104} width={40} height={20} rx={4} fill={COLORS.dave} {...line} strokeWidth={4} />
    <rect x={-10} y={-40} width={20} height={32} rx={8} fill="#cfe8f7" />
  </g>
);

export const Mailbox: React.FC<{ x: number; y: number; flag?: number }> = ({ x, y, flag = 0 }) => (
  <g>
    <rect x={x - 10} y={y - 200} width={20} height={200} fill="#8a5a33" {...line} strokeWidth={5} />
    <path d={`M${x - 90} ${y - 200} L${x - 90} ${y - 280} Q${x - 90} ${y - 330} ${x - 40} ${y - 330} L${x + 60} ${y - 330} Q${x + 110} ${y - 330} ${x + 110} ${y - 280} L${x + 110} ${y - 200} Z`} fill="#4f86c6" {...line} />
    <g transform={`translate(${x + 110} ${y - 270}) rotate(${-90 * flag})`}>
      <rect x={0} y={-8} width={14} height={70} fill={COLORS.red} {...line} strokeWidth={4} />
    </g>
    <text x={x + 10} y={y - 250} textAnchor="middle" fontFamily={BANG} fontSize={36} fill="white">
      MAIL
    </text>
  </g>
);

export const Heart: React.FC<{ x: number; y: number; s?: number; o?: number }> = ({ x, y, s = 1, o = 1 }) => (
  <g transform={`translate(${x} ${y}) scale(${s})`} opacity={o}>
    <path d="M0 30 C-60 -10 -40 -60 0 -30 C40 -60 60 -10 0 30 Z" fill="#ff5d7a" {...line} strokeWidth={4} />
  </g>
);

export const Box: React.FC<{ x: number; y: number; w?: number; h?: number; label?: string }> = ({ x, y, w = 180, h = 80, label = "" }) => (
  <g>
    <rect x={x - w / 2} y={y - h} width={w} height={h} fill="#d7a868" {...line} />
    <path d={`M${x - w / 2 + 10} ${y - h + 18} L${x + w / 2 - 10} ${y - h + 18}`} stroke="#b0844b" strokeWidth={4} />
    {label ? (
      <text x={x} y={y - h / 2 + 12} textAnchor="middle" fontFamily={HAND} fontSize={30} fill={INK}>
        {label}
      </text>
    ) : null}
  </g>
);

/** Little baby doodle for photo frames. */
export const Baby: React.FC<{ x: number; y: number; s?: number; color?: string }> = ({ x, y, s = 1, color = COLORS.tara }) => (
  <g transform={`translate(${x} ${y}) scale(${s})`} fill="none" stroke={INK} strokeWidth={5} strokeLinecap="round">
    <ellipse cx={0} cy={20} rx={34} ry={24} fill={color} />
    <circle cx={0} cy={-22} r={26} fill="white" />
    <circle cx={-9} cy={-25} r={3} fill={INK} />
    <circle cx={9} cy={-25} r={3} fill={INK} />
    <path d="M-8 -12 Q0 -5 8 -12" />
    <path d="M0 -48 q8 -10 2 -16" />
  </g>
);

export const Bunting: React.FC<{ y: number; text: string }> = ({ y, text }) => {
  const colors = ["#ff5d7a", "#ffcf3d", "#5ec8c0", "#9b4fd1", "#2f7de1"];
  return (
    <g>
      <path d={`M60 ${y} Q960 ${y + 70} 1860 ${y}`} fill="none" stroke={INK} strokeWidth={4} />
      {Array.from({ length: 18 }).map((_, i) => {
        const px = 110 + i * 100;
        const t = (px - 60) / 1800;
        const py = y + 4 * 70 * t * (1 - t) * 0.5;
        return <path key={i} d={`M${px - 35} ${py} L${px + 35} ${py} L${px} ${py + 60} Z`} fill={colors[i % 5]} stroke={INK} strokeWidth={4} strokeLinejoin="round" />;
      })}
      <text x={820} y={y + 190} textAnchor="middle" fontFamily={BANG} fontSize={72} fill="#ff5d7a" stroke={INK} strokeWidth={4} paintOrder="stroke" letterSpacing={4}>
        {text}
      </text>
    </g>
  );
};
