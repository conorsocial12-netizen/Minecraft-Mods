import React from "react";
import { AbsoluteFill, useCurrentFrame } from "remotion";
import { BANG, COLORS, HAND, INK, Sfx, Stick, usePop } from "./kit";
import { Baby, Flame, Ground, Ketchup, Pepper, Plate, PopG, Table, Window } from "./props";
import { Bubble, Canvas, Caption, Chapter, Stamp } from "./ui";
import { blinkAt } from "./scenes1";

/* ---------------- Scene 3a: Baby pictures ---------------- */
const PhotoCard: React.FC<{ x: number; y: number; w: number; h: number; s: number; r?: number; children: React.ReactNode }> = ({
  x,
  y,
  w,
  h,
  s,
  r = 0,
  children,
}) => (
  <PopG x={x} y={y} s={s}>
    <g transform={`rotate(${r} ${x} ${y})`}>
      <rect x={x - w / 2} y={y - h / 2} width={w} height={h} fill="white" stroke={INK} strokeWidth={5} />
      <rect x={x - w / 2 + 12} y={y - h / 2 + 12} width={w - 24} height={h - 44} fill="#d8eefc" />
      {children}
    </g>
  </PopG>
);

export const AlbumScene: React.FC = () => {
  const f = useCurrentFrame();
  const pops = [0, 1, 2, 3, 4, 5].map((i) => usePop(12 + i * 7));
  const pDave = usePop(140, 8);
  const pMom = usePop(248);
  const pKid = usePop(325);
  const spots = [
    [520, 380],
    [690, 380],
    [860, 380],
    [520, 560],
    [690, 560],
    [860, 560],
  ];
  return (
    <AbsoluteFill>
      <Canvas bg="#f3e6ff">
        <Ground y={880} color="#e0cdee" />
        {/* album */}
        <path d="M400 230 L955 260 L955 830 L400 800 Z" fill="#fffaf0" stroke={INK} strokeWidth={6} strokeLinejoin="round" />
        <path d="M1510 230 L965 260 L965 830 L1510 800 Z" fill="#fffaf0" stroke={INK} strokeWidth={6} strokeLinejoin="round" />
        <path d="M380 220 L960 250 L1530 220 L1530 815 L960 845 L380 815 Z" fill="none" stroke="#7b4b2a" strokeWidth={10} strokeLinejoin="round" />
        <text x={690} y={310} textAnchor="middle" fontFamily={BANG} fontSize={54} fill={COLORS.tara} letterSpacing={2}>
          TARA'S BABY PICS
        </text>
        <text x={1240} y={310} textAnchor="middle" fontFamily={BANG} fontSize={54} fill={COLORS.dave} letterSpacing={2}>
          DAVE'S BABY PICS
        </text>
        {spots.map(([x, y], i) => (
          <PhotoCard key={i} x={x} y={y} w={150} h={150} s={pops[i]} r={(i % 3) * 4 - 4}>
            <Baby x={x} y={y - 10} s={0.9} />
          </PhotoCard>
        ))}
        <text x={690} y={720} textAnchor="middle" fontFamily={HAND} fontSize={48} fill={INK} opacity={pops[5]}>
          ...and 394 more
        </text>
        {f < 140 ? (
          <text x={1240} y={560} textAnchor="middle" fontFamily={BANG} fontSize={200} fill="#cdbfd9">
            ?
          </text>
        ) : null}
        <PhotoCard x={1240} y={560} w={330} h={380} s={pDave} r={3}>
          <Stick x={1210} y={680} h={230} color={COLORS.dave} look="cap" mood="neutral" armR={70} elbowR={-60} />
          <rect x={1262} y={555} width={36} height={52} rx={4} fill="#ffcf3d" stroke={INK} strokeWidth={4} />
          <path d="M1290 555 L1300 525" stroke={INK} strokeWidth={4} />
          <text x={1240} y={736} textAnchor="middle" fontFamily={HAND} fontSize={30} fill={INK}>
            Dave, age 4 (first pic)
          </text>
        </PhotoCard>
        <PopG x={1730} y={880} s={pMom}>
          <Stick x={1730} y={880} h={560} color={COLORS.mom} look="bun" mood={f > 330 ? "worried" : "happy"} flip sweat={f > 330} armR={f < 330 ? 140 : 30} elbowR={f < 330 ? -50 : 0} blink={blinkAt(f, 30)} />
        </PopG>
        <PopG x={180} y={880} s={pKid}>
          <Stick x={180} y={880} h={280} color={COLORS.dave} look="cap" mood="angry" armL={60} armR={60} elbowL={60} elbowR={60} />
        </PopG>
      </Canvas>
      <Chapter text="SIGN #2: BABY PICTURES" to={140} />
      <Caption from={12} to={138} text="Tara has like 400 baby pictures. Me? Zero." />
      <Caption from={140} to={246} text="My FIRST photo? Age four. Already walking. Holding a juice box." />
      <Bubble from={250} to={322} x={1720} y={310} dx={-260} width={560} text="The camera was broken, sweetie!" />
      <Bubble from={327} to={400} x={180} y={590} dx={170} width={420} shout text="FOR FOUR YEARS?!" size={48} />
      {[0, 1, 2, 3, 4, 5].map((i) => (
        <Sfx key={i} at={12 + i * 7} name="pop" volume={0.35} />
      ))}
      <Sfx at={140} name="ding" volume={0.5} />
      <Sfx at={248} name="pop" />
      <Sfx at={325} name="boing" />
    </AbsoluteFill>
  );
};

/* ---------------- Scene 3b: Spicy food ---------------- */
export const SpicyScene: React.FC = () => {
  const f = useCurrentFrame();
  const chew = (o: number) => (Math.sin((f + o) * 0.5) > 0 ? "happy" : "smug");
  const shake = f > 100 ? Math.sin(f * 2.2) * 5 : 0;
  return (
    <AbsoluteFill>
      <Canvas bg="#fff0dc">
        <Window x={810} y={150} w={300} h={220} sky="#ffd3a3" />
        <Ground y={880} color="#ead1b1" />
        <Stick x={420} y={880} h={600} color={COLORS.dad} look="dad" mood={chew(0)} armR={120} elbowR={-60} />
        <Stick x={720} y={880} h={575} color={COLORS.mom} look="bun" mood={chew(7)} armL={120} elbowL={-60} />
        <Stick x={1080} y={880} h={550} color={COLORS.tara} look="pigtails" mood={chew(3)} armR={120} elbowR={-60} />
        {/* Dave stands on a chair so he can see over the table */}
        <rect x={1360} y={780} width={180} height={20} fill="#a7713f" stroke={INK} strokeWidth={5} />
        <Stick x={1450 + shake} y={780} h={300} color={COLORS.dave} look="cap" mood={f > 100 ? "red" : "worried"} sweat={f > 100} armL={45} armR={45} />
        <Table x1={220} x2={1700} y={640} />
        <Plate x={420} y={650}>
          <Pepper x={400} y={640} r={-10} />
          <Pepper x={450} y={645} r={15} />
        </Plate>
        <Plate x={720} y={650}>
          <Pepper x={700} y={640} r={10} />
          <Pepper x={745} y={642} r={-12} />
        </Plate>
        <Plate x={1080} y={650}>
          <Pepper x={1060} y={640} r={-5} />
          <Pepper x={1105} y={644} r={20} />
        </Plate>
        <Flame x={420} y={610} s={0.8} t={f} />
        <Flame x={720} y={610} s={0.8} t={f + 9} />
        <Flame x={1080} y={610} s={0.8} t={f + 4} />
        <Plate x={1450} y={650}>
          <path d="M1420 648 q20 -14 40 0 q10 6 20 -2" stroke="#e5483b" strokeWidth={10} fill="none" strokeLinecap="round" />
        </Plate>
        <Ketchup x={1600} y={645} s={0.8} />
        {f > 100 ? (
          <g opacity={0.8}>
            <path d={`M1395 ${470 - (f % 30)} q-20 -20 0 -40`} stroke="#bbb" strokeWidth={6} fill="none" />
            <path d={`M1505 ${470 - ((f + 15) % 30)} q20 -20 0 -40`} stroke="#bbb" strokeWidth={6} fill="none" />
          </g>
        ) : null}
      </Canvas>
      <Chapter text="SIGN #3: SPICY FOOD" />
      <Caption from={8} to={98} text="My family eats ghost peppers like they're candy." />
      <Caption from={100} to={200} text="Meanwhile, I think ketchup is a little spicy." />
      <Bubble from={112} to={196} x={1450} y={470} dx={0} width={420} shout text="HOO! HAH! TOO SPICY!" size={46} />
      <Stamp from={112} to={200} text="MILD" x={1770} y={480} size={70} color="#ffcf3d" rotate={10} />
      <Sfx at={0} name="whoosh" volume={0.4} />
      <Sfx at={110} name="boing" />
    </AbsoluteFill>
  );
};
