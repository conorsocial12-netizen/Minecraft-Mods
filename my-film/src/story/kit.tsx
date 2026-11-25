import React from "react";
import { Easing, interpolate, spring, staticFile, useCurrentFrame, useVideoConfig } from "remotion";
import { Audio } from "@remotion/media";
import { Sequence } from "remotion";

export const INK = "#1d1b22";
export const PAPER = "#fbf6ec";
export const COLORS = {
  dave: "#2f7de1",
  dad: "#2f9e5b",
  mom: "#9b4fd1",
  tara: "#ec4f8f",
  red: "#e5483b",
  yellow: "#ffcf3d",
};

export const HAND = "PatrickHand";
export const BANG = "Bangers";

/** Clamped interpolate with a smooth ease. */
export const ease = (f: number, input: number[], output: number[]) =>
  interpolate(f, input, output, {
    extrapolateLeft: "clamp",
    extrapolateRight: "clamp",
    easing: Easing.bezier(0.33, 1, 0.68, 1),
  });

/** Springy 0 -> 1 pop starting at `start`. */
export const usePop = (start: number, damping = 11) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  return spring({ frame: frame - start, fps, config: { damping, stiffness: 180 } });
};

export type SfxName =
  | "pop"
  | "boing"
  | "scratch"
  | "drumroll"
  | "dun"
  | "whoosh"
  | "ding"
  | "page"
  | "slap"
  | "spit"
  | "tada"
  | "thud";

/** Plays a sound effect at a frame inside the current scene. */
export const Sfx: React.FC<{ at: number; name: SfxName; volume?: number }> = ({ at, name, volume = 0.6 }) => (
  <Sequence from={at} name={`sfx ${name}`} layout="none">
    <Audio src={staticFile(`audio/${name}.wav`)} volume={volume} />
  </Sequence>
);

export type Mood = "happy" | "neutral" | "shock" | "sad" | "cry" | "angry" | "smug" | "worried" | "red";
export type Look = "cap" | "bun" | "dad" | "pigtails" | "none" | "partyhat";

export type StickProps = {
  x: number;
  y: number; // feet position
  h: number; // total height in px
  color: string;
  look?: Look;
  mood?: Mood;
  armL?: number; // degrees from straight down, positive = outward
  armR?: number;
  elbowL?: number; // extra bend at elbow in degrees
  elbowR?: number;
  walk?: number; // walking phase; 0 = standing
  lean?: number; // degrees
  flip?: boolean; // face left
  squash?: number; // vertical scale (1 = normal)
  sweat?: boolean;
  blink?: boolean;
};

const rad = (d: number) => (d * Math.PI) / 180;

/** A cartoon stick figure drawn as an SVG group. */
export const Stick: React.FC<StickProps> = ({
  x,
  y,
  h,
  color,
  look = "none",
  mood = "neutral",
  armL = 20,
  armR = 20,
  elbowL = 0,
  elbowR = 0,
  walk = 0,
  lean = 0,
  flip = false,
  squash = 1,
  sweat = false,
  blink = false,
}) => {
  const r = Math.max(26, h * 0.12);
  const sw = Math.max(6, Math.min(9, h * 0.017));
  const headY = -h + r;
  const neckY = -h + r * 2;
  const shoulderY = neckY + h * 0.06;
  const hipY = -h * 0.42;
  const armLen = h * 0.3;
  const legLen = -hipY;
  const swing = Math.sin(walk) * 28;

  const arm = (side: 1 | -1, a: number, bend: number) => {
    const ex = side * Math.sin(rad(a)) * armLen * 0.5;
    const ey = shoulderY + Math.cos(rad(a)) * armLen * 0.5;
    const hx = ex + side * Math.sin(rad(a + bend)) * armLen * 0.5;
    const hy = ey + Math.cos(rad(a + bend)) * armLen * 0.5;
    return `M0 ${shoulderY} L${ex} ${ey} L${hx} ${hy}`;
  };
  const leg = (side: 1 | -1, a: number) => {
    const fx = side * Math.sin(rad(a)) * legLen;
    const fy = hipY + Math.cos(rad(a)) * legLen;
    return `M0 ${hipY} L${fx} ${fy}`;
  };

  const eyeDx = r * 0.36;
  const eyeY = headY - r * 0.12;
  const bigEyes = mood === "shock";
  const mouthY = headY + r * 0.42;

  let mouth: React.ReactNode = null;
  switch (mood) {
    case "happy":
      mouth = <path d={`M${-r * 0.45} ${mouthY - r * 0.12} Q0 ${mouthY + r * 0.42} ${r * 0.45} ${mouthY - r * 0.12}`} />;
      break;
    case "smug":
      mouth = <path d={`M${-r * 0.35} ${mouthY} Q${r * 0.2} ${mouthY + r * 0.15} ${r * 0.45} ${mouthY - r * 0.2}`} />;
      break;
    case "shock":
      mouth = <ellipse cx={0} cy={mouthY + r * 0.05} rx={r * 0.2} ry={r * 0.3} fill={INK} />;
      break;
    case "sad":
    case "cry":
    case "worried":
      mouth = <path d={`M${-r * 0.4} ${mouthY + r * 0.15} Q0 ${mouthY - r * 0.25} ${r * 0.4} ${mouthY + r * 0.15}`} />;
      break;
    case "angry":
      mouth = <path d={`M${-r * 0.4} ${mouthY + r * 0.1} L${r * 0.4} ${mouthY + r * 0.1}`} />;
      break;
    case "red":
      mouth = <path d={`M${-r * 0.45} ${mouthY} q${r * 0.15} ${-r * 0.2} ${r * 0.3} 0 q${r * 0.15} ${r * 0.2} ${r * 0.3} 0 q${r * 0.15} ${-r * 0.2} ${r * 0.3} 0`} />;
      break;
    default:
      mouth = <path d={`M${-r * 0.3} ${mouthY} L${r * 0.3} ${mouthY}`} />;
  }

  const brows =
    mood === "angry" ? (
      <path d={`M${-eyeDx - r * 0.2} ${eyeY - r * 0.4} L${-eyeDx + r * 0.2} ${eyeY - r * 0.22} M${eyeDx + r * 0.2} ${eyeY - r * 0.4} L${eyeDx - r * 0.2} ${eyeY - r * 0.22}`} />
    ) : mood === "sad" || mood === "cry" || mood === "worried" ? (
      <path d={`M${-eyeDx - r * 0.2} ${eyeY - r * 0.25} L${-eyeDx + r * 0.2} ${eyeY - r * 0.4} M${eyeDx + r * 0.2} ${eyeY - r * 0.25} L${eyeDx - r * 0.2} ${eyeY - r * 0.4}`} />
    ) : mood === "shock" ? (
      <path d={`M${-eyeDx - r * 0.2} ${eyeY - r * 0.5} Q${-eyeDx} ${eyeY - r * 0.65} ${-eyeDx + r * 0.2} ${eyeY - r * 0.5} M${eyeDx - r * 0.2} ${eyeY - r * 0.5} Q${eyeDx} ${eyeY - r * 0.65} ${eyeDx + r * 0.2} ${eyeY - r * 0.5}`} />
    ) : null;

  const eyes = blink ? (
    <path d={`M${-eyeDx - r * 0.12} ${eyeY} L${-eyeDx + r * 0.12} ${eyeY} M${eyeDx - r * 0.12} ${eyeY} L${eyeDx + r * 0.12} ${eyeY}`} />
  ) : mood === "smug" ? (
    <path d={`M${-eyeDx - r * 0.14} ${eyeY} L${-eyeDx + r * 0.14} ${eyeY} M${eyeDx - r * 0.14} ${eyeY} L${eyeDx + r * 0.14} ${eyeY}`} />
  ) : (
    <>
      <circle cx={-eyeDx} cy={eyeY} r={bigEyes ? r * 0.2 : r * 0.1} fill={bigEyes ? "white" : INK} stroke={bigEyes ? INK : "none"} strokeWidth={sw * 0.6} />
      <circle cx={eyeDx} cy={eyeY} r={bigEyes ? r * 0.2 : r * 0.1} fill={bigEyes ? "white" : INK} stroke={bigEyes ? INK : "none"} strokeWidth={sw * 0.6} />
      {bigEyes ? (
        <>
          <circle cx={-eyeDx} cy={eyeY} r={r * 0.07} fill={INK} />
          <circle cx={eyeDx} cy={eyeY} r={r * 0.07} fill={INK} />
        </>
      ) : null}
    </>
  );

  let hat: React.ReactNode = null;
  if (look === "cap") {
    hat = (
      <g fill={color} stroke={INK} strokeWidth={sw * 0.8}>
        <path d={`M${-r} ${headY - r * 0.15} A${r} ${r} 0 0 1 ${r} ${headY - r * 0.15} Z`} />
        <path d={`M${r * 0.6} ${headY - r * 0.2} L${r * 1.6} ${headY - r * 0.2} L${r * 1.5} ${headY - r * 0.02} L${r * 0.6} ${headY - r * 0.02} Z`} />
      </g>
    );
  } else if (look === "bun") {
    hat = (
      <g fill={color} stroke={INK} strokeWidth={sw * 0.8}>
        <circle cx={0} cy={headY - r * 1.25} r={r * 0.45} />
        <path d={`M${-r} ${headY} A${r} ${r} 0 0 1 ${r} ${headY} Q${r * 0.4} ${headY - r * 0.55} ${-r} ${headY} Z`} />
      </g>
    );
  } else if (look === "dad") {
    hat = (
      <g>
        <path d={`M${-r * 0.55} ${mouthY - r * 0.18} Q${-r * 0.25} ${mouthY - r * 0.42} 0 ${mouthY - r * 0.22} Q${r * 0.25} ${mouthY - r * 0.42} ${r * 0.55} ${mouthY - r * 0.18}`} fill={INK} stroke={INK} strokeWidth={sw * 0.6} />
        <path d={`M${-r * 0.5} ${headY - r * 0.9} L${-r * 0.4} ${headY - r * 1.3} M0 ${headY - r} L0 ${headY - r * 1.45} M${r * 0.5} ${headY - r * 0.9} L${r * 0.4} ${headY - r * 1.3}`} stroke={color} strokeWidth={sw * 0.9} />
      </g>
    );
  } else if (look === "pigtails") {
    hat = (
      <g fill={color} stroke={INK} strokeWidth={sw * 0.8}>
        <ellipse cx={-r * 1.25} cy={headY - r * 0.3} rx={r * 0.35} ry={r * 0.55} />
        <ellipse cx={r * 1.25} cy={headY - r * 0.3} rx={r * 0.35} ry={r * 0.55} />
        <path d={`M${-r} ${headY - r * 0.1} A${r} ${r} 0 0 1 ${r} ${headY - r * 0.1} Q0 ${headY - r * 0.6} ${-r} ${headY - r * 0.1} Z`} />
      </g>
    );
  } else if (look === "partyhat") {
    hat = (
      <g stroke={INK} strokeWidth={sw * 0.8}>
        <path d={`M${-r * 0.6} ${headY - r * 0.75} L${r * 0.1} ${headY - r * 2.2} L${r * 0.7} ${headY - r * 0.65} Z`} fill={COLORS.yellow} />
        <circle cx={r * 0.1} cy={headY - r * 2.25} r={r * 0.18} fill={COLORS.red} />
      </g>
    );
  }

  return (
    <g transform={`translate(${x} ${y}) scale(${flip ? -1 : 1} ${squash}) rotate(${lean})`}>
      <g fill="none" stroke={INK} strokeWidth={sw} strokeLinecap="round" strokeLinejoin="round">
        {/* body + shirt colour stripe */}
        <path d={`M0 ${neckY} L0 ${hipY}`} />
        <path d={`M0 ${neckY + sw} L0 ${hipY - sw}`} stroke={color} strokeWidth={sw * 0.45} />
        <path d={arm(-1, armL, elbowL)} />
        <path d={arm(1, armR, elbowR)} />
        <path d={leg(-1, 12 + swing)} />
        <path d={leg(1, 12 - swing)} />
        <circle cx={0} cy={headY} r={r} fill="white" />
        {mood === "red" ? <circle cx={0} cy={headY} r={r - sw / 2} fill="#ff8a7a" stroke="none" /> : null}
        <g strokeWidth={sw * 0.7}>
          {eyes}
          {brows}
          {mouth}
        </g>
        {mood === "happy" || mood === "smug" ? (
          <g fill="#ffb3b3" stroke="none" opacity={0.7}>
            <circle cx={-r * 0.6} cy={headY + r * 0.25} r={r * 0.13} />
            <circle cx={r * 0.6} cy={headY + r * 0.25} r={r * 0.13} />
          </g>
        ) : null}
        {mood === "cry" || mood === "red" ? (
          <g fill="#6fc3ff" stroke="none">
            <path d={`M${-eyeDx} ${eyeY + r * 0.15} q${-r * 0.12} ${r * 0.3} 0 ${r * 0.38} q${r * 0.12} ${-r * 0.08} 0 ${-r * 0.38}`} />
            <path d={`M${eyeDx} ${eyeY + r * 0.15} q${-r * 0.12} ${r * 0.3} 0 ${r * 0.38} q${r * 0.12} ${-r * 0.08} 0 ${-r * 0.38}`} />
          </g>
        ) : null}
        {sweat ? (
          <path d={`M${r * 1.05} ${headY - r * 0.7} q${-r * 0.15} ${r * 0.3} 0 ${r * 0.4} q${r * 0.15} ${-r * 0.1} 0 ${-r * 0.4}`} fill="#6fc3ff" stroke="none" />
        ) : null}
        {hat}
      </g>
    </g>
  );
};

/** A little stick dog. */
export const Dog: React.FC<{ x: number; y: number; s?: number; flip?: boolean; wag?: number; shock?: boolean }> = ({
  x,
  y,
  s = 1,
  flip = false,
  wag = 0,
  shock = false,
}) => (
  <g transform={`translate(${x} ${y}) scale(${(flip ? -1 : 1) * s} ${s})`} fill="none" stroke={INK} strokeWidth={7} strokeLinecap="round" strokeLinejoin="round">
    <path d="M-60 -60 L40 -60" />
    <path d="M-55 -60 L-65 0 M-35 -60 L-30 0 M20 -60 L15 0 M38 -60 L45 0" />
    <path d={`M-60 -60 L-90 ${-95 + wag * 12}`} />
    <path d="M40 -60 L55 -95" />
    <ellipse cx={75} cy={-105} rx={34} ry={26} fill="#f3d9b1" />
    <path d="M55 -125 Q45 -95 58 -88" fill="#a06a3c" />
    <circle cx={88} cy={-112} r={shock ? 7 : 4} fill={shock ? "white" : INK} />
    {shock ? <circle cx={88} cy={-112} r={3} fill={INK} /> : null}
    <circle cx={108} cy={-102} r={6} fill={INK} />
  </g>
);
