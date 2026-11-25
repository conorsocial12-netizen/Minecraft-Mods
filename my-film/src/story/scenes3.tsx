import React from "react";
import { AbsoluteFill, interpolate, Sequence, useCurrentFrame } from "remotion";
import { BANG, COLORS, ease, HAND, INK, Sfx, Stick, usePop } from "./kit";
import { Bunting, Gift, Ground, Mailbox, PopG, Tube } from "./props";
import { Bubble, Canvas, Caption, Chapter, Stamp } from "./ui";
import { blinkAt } from "./scenes1";

/* ---- 4a: birthday gift ---- */
const Birthday: React.FC = () => {
  const f = useCurrentFrame();
  const gx = ease(f, [20, 55], [1200, 840]);
  const gy = ease(f, [20, 55], [560, 700]);
  return (
    <AbsoluteFill>
      <Canvas bg="#e2f6f3">
        <Bunting y={60} text="HAPPY BIRTHDAY DAVE!" />
        <Ground y={880} color="#c9e7e1" />
        <Stick x={1300} y={880} h={550} color={COLORS.tara} look="pigtails" flip mood="happy" armR={f < 55 ? 75 : 25} elbowR={f < 55 ? 20 : 0} blink={blinkAt(f, 40)} />
        <Stick x={760} y={880} h={300} color={COLORS.dave} look="partyhat" mood={f > 70 ? "smug" : "happy"} armR={f > 50 ? 60 : 20} elbowR={f > 50 ? 30 : 0} blink={blinkAt(f, 10)} />
        <Gift x={gx} y={gy} s={0.9} />
      </Canvas>
      <Chapter text="THE DNA TEST" to={150} />
      <Caption from={4} to={150} text="So for my 34th birthday, my sister gets me a DNA test. As a JOKE." />
      <Bubble from={40} to={146} x={1300} y={320} dx={250} width={560} text="Haha, maybe you're secretly a Viking!" />
      <Sfx at={20} name="whoosh" volume={0.4} />
    </AbsoluteFill>
  );
};

/* ---- 4b: spit + mail ---- */
const Spit: React.FC = () => {
  const f = useCurrentFrame();
  const tubeX = ease(f, [65, 85], [925, 1460]);
  const tubeY = interpolate(f, [65, 75, 85], [690, 380, 560], { extrapolateLeft: "clamp", extrapolateRight: "clamp" });
  const tubeR = ease(f, [65, 85], [0, 360]);
  const dropT = interpolate(f, [30, 40], [0, 1], { extrapolateLeft: "clamp", extrapolateRight: "clamp" });
  return (
    <AbsoluteFill>
      <Canvas bg="#eef5e6">
        <Ground y={880} color="#d6e6c6" />
        <Mailbox x={1450} y={880} flag={ease(f, [86, 96], [0, 1])} />
        <Stick x={820} y={880} h={360} color={COLORS.dave} look="cap" mood={f > 28 && f < 45 ? "shock" : "neutral"} armR={f < 65 ? 75 : 110} elbowR={f < 65 ? -20 : -60} blink={blinkAt(f)} />
        {f < 86 ? <Tube x={f < 65 ? 925 : tubeX} y={f < 65 ? 690 : tubeY} r={f < 65 ? 0 : tubeR} /> : null}
        {f >= 30 && f < 41 ? <circle cx={interpolate(dropT, [0, 1], [850, 925])} cy={interpolate(dropT, [0, 0.5, 1], [590, 560, 610])} r={10} fill="#bfe6ff" stroke={INK} strokeWidth={3} /> : null}
      </Canvas>
      <Caption from={4} to={110} text="So I spit in the little tube. Mail it off. Forget about it." />
      <Sfx at={28} name="spit" />
      <Sfx at={65} name="whoosh" volume={0.5} />
      <Sfx at={86} name="thud" volume={0.4} />
    </AbsoluteFill>
  );
};

/* ---- 4c: weeks later ---- */
const WeeksLater: React.FC = () => {
  const f = useCurrentFrame();
  const page = Math.min(6, Math.floor(f / 12) + 1);
  return (
    <AbsoluteFill>
      <Canvas bg="#2b2a3a">
        <g transform={`rotate(-3 960 520)`}>
          <rect x={710} y={260} width={500} height={480} rx={20} fill="white" stroke={INK} strokeWidth={8} />
          <rect x={710} y={260} width={500} height={120} rx={20} fill={COLORS.red} stroke={INK} strokeWidth={8} />
          <text x={960} y={345} textAnchor="middle" fontFamily={BANG} fontSize={70} fill="white" letterSpacing={3}>
            WEEK
          </text>
          <text x={960} y={640} textAnchor="middle" fontFamily={BANG} fontSize={250} fill={INK}>
            {page}
          </text>
        </g>
      </Canvas>
      <Stamp from={70} text="6 WEEKS LATER..." y={880} size={110} color="white" rotate={-2} />
      {[0, 12, 24, 36, 48, 60].map((t) => (
        <Sfx key={t} at={t} name="page" volume={0.6} />
      ))}
    </AbsoluteFill>
  );
};

/* ---- 4d: the results ---- */
const Results: React.FC = () => {
  const f = useCurrentFrame();
  const load = ease(f, [0, 70], [0, 1]);
  const l1 = usePop(75);
  const l2 = usePop(145, 9);
  const l3 = usePop(220);
  const zoom = interpolate(f, [145, 165], [1, 1.12], { extrapolateLeft: "clamp", extrapolateRight: "clamp" });
  const shake = f > 145 && f < 165 ? Math.sin(f * 3) * 8 : 0;
  return (
    <AbsoluteFill style={{ scale: String(zoom), translate: `${shake}px 0px` }}>
      <Canvas bg="#dfe7f5">
        <Ground y={900} color="#c3cfe3" />
        {/* laptop */}
        <rect x={360} y={120} width={1080} height={660} rx={30} fill="#3b3d4a" stroke={INK} strokeWidth={8} />
        <rect x={395} y={155} width={1010} height={590} rx={10} fill="white" />
        <path d="M270 780 L1530 780 L1590 850 L210 850 Z" fill="#8f93a6" stroke={INK} strokeWidth={8} strokeLinejoin="round" />
        <text x={900} y={240} textAnchor="middle" fontFamily={BANG} fontSize={72} fill={INK} letterSpacing={3}>
          YOUR DNA RESULTS
        </text>
        {f < 75 ? (
          <g>
            <rect x={600} y={420} width={600} height={50} rx={25} fill="#eee" stroke={INK} strokeWidth={5} />
            <rect x={605} y={425} width={590 * load} height={40} rx={20} fill={COLORS.dave} />
            <text x={900} y={530} textAnchor="middle" fontFamily={HAND} fontSize={44} fill={INK}>
              Loading...
            </text>
          </g>
        ) : null}
        <PopG x={900} y={340} s={l1}>
          <text x={900} y={360} textAnchor="middle" fontFamily={HAND} fontSize={64} fill={INK}>
            Ancestry: 12% Viking
          </text>
        </PopG>
        <PopG x={900} y={480} s={l2}>
          <rect x={470} y={420} width={860} height={110} rx={16} fill="#ffe1dd" stroke={COLORS.red} strokeWidth={6} />
          <text x={900} y={495} textAnchor="middle" fontFamily={BANG} fontSize={64} fill={COLORS.red} letterSpacing={2}>
            MATCHES WITH YOUR PARENTS: 0
          </text>
        </PopG>
        <PopG x={900} y={640} s={l3}>
          <text x={900} y={630} textAnchor="middle" fontFamily={HAND} fontSize={56} fill={INK}>
            New relatives found: 23
          </text>
          <text x={900} y={695} textAnchor="middle" fontFamily={HAND} fontSize={56} fill={COLORS.dave}>
            The Henderson family (Ohio)
          </text>
        </PopG>
        <Stick x={1680} y={900} h={320} color={COLORS.dave} look="cap" flip mood={f > 145 ? "shock" : f > 75 ? "smug" : "worried"} sweat={f > 145} armL={f > 145 ? 150 : 20} armR={f > 145 ? 150 : 20} blink={f < 145 && blinkAt(f)} />
      </Canvas>
      <Caption from={78} to={143} text="12% Viking. Okay. Cool. Tara was right." />
      <Caption from={147} to={218} text="Close family matches with my parents... ZERO." />
      <Caption from={222} to={300} text="But apparently I had 23 cousins. In OHIO." />
      <Sfx at={0} name="drumroll" volume={0.55} />
      <Sfx at={145} name="dun" volume={0.8} />
      <Sfx at={220} name="ding" volume={0.5} />
    </AbsoluteFill>
  );
};

export const DnaScene: React.FC = () => (
  <AbsoluteFill>
    <Sequence durationInFrames={150} name="Birthday">
      <Birthday />
    </Sequence>
    <Sequence from={150} durationInFrames={110} name="Spit">
      <Spit />
    </Sequence>
    <Sequence from={260} durationInFrames={100} name="Weeks later">
      <WeeksLater />
    </Sequence>
    <Sequence from={360} durationInFrames={300} name="Results">
      <Results />
    </Sequence>
  </AbsoluteFill>
);
