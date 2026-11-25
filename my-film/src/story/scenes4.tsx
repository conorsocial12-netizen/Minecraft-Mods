import React from "react";
import { AbsoluteFill, interpolate, useCurrentFrame } from "remotion";
import { BANG, COLORS, Dog, ease, INK, Sfx, Stick, usePop } from "./kit";
import { Box, Drumstick, Ground, Heart, Ketchup, PopG, Table, Turkey, Window } from "./props";
import { Bubble, Canvas, Caption, Chapter, Stamp } from "./ui";
import { blinkAt } from "./scenes1";

/* ---------------- Scene 5: Thanksgiving ---------------- */
export const ThanksgivingScene: React.FC = () => {
  const f = useCurrentFrame();
  const shocked = f >= 136;
  const paper = usePop(128, 9);
  // Dad's drumstick falls from his hand to the floor
  const legY = interpolate(f, [150, 168], [560, 860], { extrapolateLeft: "clamp", extrapolateRight: "clamp" });
  const legR = interpolate(f, [150, 168], [-20, 160], { extrapolateLeft: "clamp", extrapolateRight: "clamp" });
  const chew = (o: number) => (Math.sin((f + o) * 0.5) > 0 ? "happy" : "smug");
  return (
    <AbsoluteFill>
      <Canvas bg="#fbe3c8">
        <Window x={180} y={130} w={260} h={220} sky="#ffb36b" />
        <Window x={1480} y={130} w={260} h={220} sky="#ffb36b" />
        <Ground y={880} color="#c99a6b" />
        <Stick x={420} y={880} h={600} color={COLORS.dad} look="dad" mood={shocked ? "shock" : chew(0)} armR={shocked ? 60 : 110} elbowR={shocked ? 0 : -60} blink={!shocked && blinkAt(f, 20)} />
        <Stick x={720} y={880} h={575} color={COLORS.mom} look="bun" mood={f > 250 ? "worried" : shocked ? "shock" : chew(5)} sweat={f > 250} blink={!shocked && blinkAt(f, 60)} />
        <Stick x={1180} y={880} h={550} color={COLORS.tara} look="pigtails" mood={f > 495 ? "smug" : shocked ? "shock" : chew(9)} blink={!shocked && blinkAt(f, 90)} />
        {/* Dave stands on his chair */}
        <rect x={1420} y={780} width={180} height={20} fill="#a7713f" stroke={INK} strokeWidth={5} />
        <Stick
          x={1510}
          y={780}
          h={300}
          color={COLORS.dave}
          look="cap"
          mood={f > 335 ? "angry" : f > 120 ? "smug" : "neutral"}
          flip
          armR={f > 115 && f < 140 ? 100 : 25}
          elbowR={f > 115 && f < 140 ? 40 : 0}
          blink={blinkAt(f)}
        />
        <Table x1={240} x2={1680} y={640} cloth="#fff5e0" />
        <Turkey x={900} y={630} legless={f >= 150} />
        {f < 150 ? <Drumstick x={500} y={560} r={-20} s={0.8} /> : f < 168 ? <Drumstick x={500} y={legY} r={legR} s={0.8} /> : null}
        <PopG x={1320} y={640} s={paper}>
          <g transform="rotate(-6 1320 620)">
            <rect x={1230} y={590} width={180} height={60} fill="white" stroke={INK} strokeWidth={5} />
            <text x={1320} y={632} textAnchor="middle" fontFamily={BANG} fontSize={30} fill={COLORS.red}>
              DNA RESULTS
            </text>
          </g>
        </PopG>
        <Dog x={1770} y={880} s={0.85} flip shock={shocked} wag={shocked ? 0 : Math.sin(f * 0.6)} />
        {f >= 168 ? <Drumstick x={1690} y={855} r={95} s={0.7} /> : null}
      </Canvas>
      <Chapter text="THANKSGIVING" to={130} />
      <Caption from={6} to={126} text="So I waited for Thanksgiving. Whole family at the table. Perfect moment..." />
      <Stamp from={130} to={176} text="SLAP!" x={1320} y={480} size={110} color="white" rotate={8} />
      <Caption from={140} to={248} text="Silence. Dad drops his turkey leg. The dog grabs it." />
      <Bubble from={250} to={335} x={720} y={290} dx={200} width={560} text="We were going to tell you!" />
      <Bubble from={338} to={405} x={1510} y={470} dx={-180} width={340} shout text="WHEN?!" size={60} />
      <Bubble from={408} to={495} x={720} y={290} dx={200} width={560} text="...on your 40th birthday?" />
      <Bubble from={498} to={588} x={1180} y={315} dx={-200} width={520} text="I've known since I was six." />
      <Bubble from={590} to={648} x={1510} y={470} dx={-160} width={300} shout text="SIX?!" size={60} />
      <Bubble from={652} to={720} x={1830} y={740} dx={-290} width={440} thought text="...I knew too." />
      <Sfx at={128} name="slap" volume={0.9} />
      <Sfx at={136} name="scratch" volume={0.7} />
      <Sfx at={168} name="thud" volume={0.6} />
      <Sfx at={338} name="boing" volume={0.4} />
      <Sfx at={590} name="boing" volume={0.4} />
    </AbsoluteFill>
  );
};

/* ---------------- Scene 6: We picked you ---------------- */
export const HeartScene: React.FC = () => {
  const f = useCurrentFrame();
  const hug = ease(f, [300, 340], [0, 1]);
  const box = usePop(296, 10);
  const daveLift = box * 80;
  const hearts = [0, 1, 2, 3, 4, 5, 6, 7];
  return (
    <AbsoluteFill>
      <Canvas bg="#ffe3d6">
        <circle cx={960} cy={1000} r={620} fill="#ffd0bd" />
        <Ground y={880} color="#f5c4ad" />
        <Stick x={interpolate(hug, [0, 1], [400, 640])} y={880} h={600} color={COLORS.dad} look="dad" mood="happy" armR={interpolate(hug, [0, 1], [20, 75])} blink={blinkAt(f, 20)} />
        <Stick
          x={interpolate(hug, [0, 1], [780, 850])}
          y={880}
          h={575}
          color={COLORS.mom}
          look="bun"
          mood={f > 150 && f < 240 ? "smug" : "happy"}
          lean={interpolate(hug, [0, 1], [12, 0])}
          armR={interpolate(hug, [0, 1], [60, 80])}
          armL={interpolate(hug, [0, 1], [20, 75])}
          blink={blinkAt(f, 50)}
        />
        <PopG x={1080} y={880} s={box}>
          <Box x={1080} y={880} w={170} h={80} label="for photos" />
        </PopG>
        <Stick x={1080} y={880 - daveLift} h={300} color={COLORS.dave} look="cap" mood={f > 300 ? "happy" : f > 240 ? "smug" : "cry"} armL={interpolate(hug, [0, 1], [20, 80])} armR={interpolate(hug, [0, 1], [20, 80])} blink={blinkAt(f)} />
        <Stick
          x={interpolate(hug, [0, 1], [1450, 1300])}
          y={880}
          h={550}
          color={COLORS.tara}
          look="pigtails"
          mood={f > 240 && f < 300 ? "angry" : "happy"}
          flip
          armR={interpolate(hug, [0, 1], [20, 75])}
          armL={f > 240 && f < 300 ? 140 : 20}
          blink={blinkAt(f, 80)}
        />
        {hearts.map((i) => {
          const start = 310 + i * 12;
          const t = f - start;
          if (t < 0) return null;
          const hx = 560 + ((i * 263) % 900);
          return <Heart key={i} x={hx + Math.sin(t * 0.1 + i) * 20} y={520 - t * 2.2} s={0.6 + (i % 3) * 0.2} o={interpolate(t, [0, 10, 90, 110], [0, 1, 1, 0], { extrapolateRight: "clamp" })} />;
        })}
      </Canvas>
      <Bubble from={18} to={148} x={790} y={295} dx={180} width={700} text="Honey, we PICKED you. Out of every baby in the world, we chose YOU." size={52} />
      <Bubble from={152} to={238} x={790} y={295} dx={180} width={640} text="Your sister? We just kinda got stuck with her." size={52} />
      <Bubble from={242} to={298} x={1450} y={320} dx={-150} width={340} shout text="HEY!!" size={60} />
      <Caption from={305} to={420} text="So yeah. I'm adopted. But they're still my family. I just need a box for the photos." />
      <Sfx at={242} name="boing" volume={0.5} />
      <Sfx at={296} name="pop" />
    </AbsoluteFill>
  );
};

/* ---------------- Scene 7: The Hendersons + outro ---------------- */
export const OutroScene: React.FC = () => {
  const f = useCurrentFrame();
  const pops = [0, 1, 2, 3, 4].map((i) => usePop(110 + i * 6));
  const ketchup = usePop(140);
  const end = f >= 210;
  const wave = (o: number) => (end ? 150 + Math.sin((f + o) * 0.45) * 22 : 20);
  const shiftY = ease(f, [205, 225], [0, 90]);
  return (
    <AbsoluteFill>
      <Canvas bg="#e3f0ff">
        <Ground y={900 + shiftY} color="#c8dcf5" />
        <g transform={`translate(0 ${shiftY})`}>
          <Stick x={330} y={900} h={300} color={COLORS.dave} look="cap" mood="happy" armR={wave(0)} elbowR={end ? -30 : 0} blink={blinkAt(f)} />
          {[0, 1, 2, 3, 4].map((i) => {
            const x = 740 + i * 250;
            return (
              <PopG key={i} x={x} y={900} s={pops[i]}>
                <Stick x={x} y={900} h={300} color={COLORS.dave} look="cap" mood={end ? "happy" : f > 150 ? "red" : "happy"} sweat={!end && f > 150} armR={wave(i * 7)} elbowR={end ? -30 : 0} blink={blinkAt(f, i * 23)} />
              </PopG>
            );
          })}
          {!end ? (
            <PopG x={1365} y={560} s={ketchup}>
              <rect x={1295} y={560} width={140} height={22} rx={6} fill="#a7713f" stroke="#1d1b22" strokeWidth={5} />
              <path d="M1365 582 L1365 900" stroke="#1d1b22" strokeWidth={8} />
              <Ketchup x={1365} y={560} s={0.9} />
            </PopG>
          ) : null}
        </g>
        {end ? (
          <g>
            <rect x={760} y={400} width={400} height={110} rx={18} fill={COLORS.red} stroke={INK} strokeWidth={7} />
            <text x={960} y={478} textAnchor="middle" fontFamily={BANG} fontSize={66} fill="white" letterSpacing={3}>
              SUBSCRIBE
            </text>
          </g>
        ) : null}
      </Canvas>
      <Bubble from={8} to={108} x={330} y={590} dx={260} width={620} text="Oh, and I finally met the Hendersons." />
      <Caption from={112} to={206} text={`Every single one of them is 5'2". And they ALL think ketchup is spicy.`} />
      <Stamp from={210} text="LIKE & SUBSCRIBE!" y={220} size={150} />
      <Stamp from={220} text="for more story time" y={580} size={64} color="white" rotate={0} />
      {[0, 1, 2, 3, 4].map((i) => (
        <Sfx key={i} at={110 + i * 6} name="pop" volume={0.35} />
      ))}
      <Sfx at={150} name="boing" volume={0.5} />
      <Sfx at={210} name="tada" volume={0.7} />
    </AbsoluteFill>
  );
};
