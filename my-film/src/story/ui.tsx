import React from "react";
import { AbsoluteFill, interpolate, spring, useCurrentFrame, useVideoConfig } from "remotion";
import { BANG, HAND, INK, PAPER } from "./kit";

/** Full-frame SVG canvas in 1920x1080 coordinates. */
export const Canvas: React.FC<{ children: React.ReactNode; bg?: string }> = ({ children, bg = PAPER }) => (
  <AbsoluteFill style={{ backgroundColor: bg }}>
    <svg viewBox="0 0 1920 1080" width={1920} height={1080} style={{ position: "absolute", inset: 0 }}>
      {children}
    </svg>
  </AbsoluteFill>
);

/** Springy visibility for an item shown between `from` and `to` (scene frames). */
const usePopWindow = (from: number, to: number) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  const inn = spring({ frame: frame - from, fps, config: { damping: 12, stiffness: 200 } });
  const out = interpolate(frame, [to - 6, to], [1, 0], { extrapolateLeft: "clamp", extrapolateRight: "clamp" });
  const visible = frame >= from && frame < to;
  return { s: visible ? inn * out : 0, visible };
};

type BubbleProps = {
  from: number;
  to: number;
  x: number; // tail tip (speaker's mouth area)
  y: number;
  text: string;
  dx?: number; // horizontal offset of bubble centre from tail tip
  width?: number;
  size?: number;
  shout?: boolean;
  thought?: boolean;
};

/** Comic speech bubble whose tail points at (x, y). */
export const Bubble: React.FC<BubbleProps> = ({ from, to, x, y, text, dx = 0, width = 560, size = 56, shout = false, thought = false }) => {
  const { s, visible } = usePopWindow(from, to);
  if (!visible) return null;
  const gap = 70;
  return (
    <AbsoluteFill style={{ pointerEvents: "none" }}>
      <div
        style={{
          position: "absolute",
          left: x + dx - width / 2,
          bottom: 1080 - (y - gap),
          width,
          scale: String(s),
          transformOrigin: `${width / 2 - dx}px calc(100% + ${gap}px)`,
        }}
      >
        <div
          style={{
            background: shout ? "#fff3a8" : "white",
            border: `6px solid ${INK}`,
            borderRadius: thought ? 80 : 40,
            padding: "22px 34px",
            fontFamily: shout ? BANG : HAND,
            fontSize: shout ? size * 1.25 : size,
            letterSpacing: shout ? 2 : 0,
            lineHeight: 1.12,
            color: INK,
            textAlign: "center",
            boxShadow: `8px 8px 0 ${INK}`,
          }}
        >
          {text}
        </div>
        {thought ? (
          <>
            <div style={{ position: "absolute", left: width / 2 - dx - 22, top: "calc(100% + 14px)", width: 34, height: 34, borderRadius: 20, border: `5px solid ${INK}`, background: "white" }} />
            <div style={{ position: "absolute", left: width / 2 - dx - 8, top: "calc(100% + 52px)", width: 18, height: 18, borderRadius: 12, border: `4px solid ${INK}`, background: "white" }} />
          </>
        ) : (
          <svg
            width={120}
            height={gap + 8}
            viewBox={`0 0 120 ${gap + 8}`}
            style={{ position: "absolute", left: width / 2 - dx - 60, top: "100%", marginTop: -8, overflow: "visible" }}
          >
            <path d={`M${dx > 0 ? 40 : 30} 0 L60 ${gap} L${dx > 0 ? 90 : 80} 0`} fill={shout ? "#fff3a8" : "white"} stroke={INK} strokeWidth={6} strokeLinejoin="round" />
            <rect x={dx > 0 ? 43 : 33} y={-12} width={44} height={15} fill={shout ? "#fff3a8" : "white"} />
          </svg>
        )}
      </div>
    </AbsoluteFill>
  );
};

/** Narrator caption strip near the bottom of the frame. */
export const Caption: React.FC<{ from: number; to: number; text: string; top?: boolean }> = ({ from, to, text, top = false }) => {
  const frame = useCurrentFrame();
  if (frame < from || frame >= to) return null;
  const o = interpolate(frame, [from, from + 6, to - 6, to], [0, 1, 1, 0], { extrapolateLeft: "clamp", extrapolateRight: "clamp" });
  const ty = interpolate(frame, [from, from + 8], [20, 0], { extrapolateLeft: "clamp", extrapolateRight: "clamp" });
  return (
    <AbsoluteFill style={{ justifyContent: top ? "flex-start" : "flex-end", alignItems: "center", padding: top ? "70px 120px" : "0 120px 70px" }}>
      <div
        style={{
          opacity: o,
          translate: `0px ${ty}px`,
          background: INK,
          color: "white",
          fontFamily: HAND,
          fontSize: 54,
          lineHeight: 1.15,
          padding: "16px 40px",
          borderRadius: 22,
          maxWidth: 1500,
          textAlign: "center",
        }}
      >
        {text}
      </div>
    </AbsoluteFill>
  );
};

/** Big cartoon title text that slams in. */
export const Stamp: React.FC<{
  from: number;
  to?: number;
  text: string;
  x?: number;
  y?: number;
  size?: number;
  color?: string;
  rotate?: number;
}> = ({ from, to = 99999, text, x = 960, y = 540, size = 150, color = "#ffcf3d", rotate = -4 }) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  if (frame < from || frame >= to) return null;
  const s = spring({ frame: frame - from, fps, config: { damping: 9, stiffness: 220 } });
  const out = interpolate(frame, [to - 6, to], [1, 0], { extrapolateLeft: "clamp", extrapolateRight: "clamp" });
  return (
    <div
      style={{
        position: "absolute",
        left: x,
        top: y,
        translate: "-50% -50%",
        scale: String(interpolate(s, [0, 1], [2.4, 1]) * out),
        opacity: Math.min(1, s * 3) * out,
        rotate: `${rotate}deg`,
        fontFamily: BANG,
        fontSize: size,
        color,
        letterSpacing: 4,
        whiteSpace: "nowrap",
        WebkitTextStroke: `${Math.round(size / 18)}px ${INK}`,
        paintOrder: "stroke fill",
        textShadow: `${size / 22}px ${size / 22}px 0 ${INK}`,
      }}
    >
      {text}
    </div>
  );
};

/** Small chapter tag in the top-left corner. */
export const Chapter: React.FC<{ text: string; from?: number; to?: number }> = ({ text, from = 0, to = 99999 }) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  if (frame < from || frame >= to) return null;
  const s = spring({ frame: frame - from, fps, config: { damping: 14 } });
  return (
    <div
      style={{
        position: "absolute",
        left: 80,
        top: 60,
        translate: `${interpolate(s, [0, 1], [-500, 0])}px 0px`,
        background: "#ffcf3d",
        border: `6px solid ${INK}`,
        boxShadow: `6px 6px 0 ${INK}`,
        borderRadius: 16,
        padding: "8px 28px",
        fontFamily: BANG,
        fontSize: 64,
        letterSpacing: 3,
        color: INK,
        rotate: "-3deg",
      }}
    >
      {text}
    </div>
  );
};

/** Label tag drawn inside SVG. */
export const SvgTag: React.FC<{ x: number; y: number; text: string; s: number; color?: string; size?: number }> = ({
  x,
  y,
  text,
  s,
  color = "white",
  size = 48,
}) => {
  if (s <= 0.001) return null;
  const w = text.length * size * 0.52 + 40;
  return (
    <g transform={`translate(${x} ${y}) scale(${s})`}>
      <rect x={-w / 2} y={-size * 0.85} width={w} height={size * 1.35} rx={14} fill={color} stroke={INK} strokeWidth={5} />
      <text x={0} y={size * 0.25} textAnchor="middle" fontFamily={BANG} fontSize={size} fill={INK} letterSpacing={2}>
        {text}
      </text>
    </g>
  );
};
