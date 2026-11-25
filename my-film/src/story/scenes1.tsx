import React from "react";
import { AbsoluteFill, interpolate, useCurrentFrame } from "remotion";
import { COLORS, ease, INK, Sfx, Stick, usePop } from "./kit";
import { Frame, Ground, Plant, PopG } from "./props";
import { Bubble, Canvas, Caption, Chapter, Stamp, SvgTag } from "./ui";

export const blinkAt = (f: number, offset = 0) => (f + offset) % 110 < 4;

/* ---------------- Scene 1: Intro ---------------- */
export const IntroScene: React.FC = () => {
  const f = useCurrentFrame();
  const x = ease(f, [30, 90], [-200, 960]);
  const walking = f > 30 && f < 90;
  const wave = f > 92 && f < 175 ? 150 + Math.sin(f * 0.45) * 22 : 20;
  return (
    <AbsoluteFill>
      <Canvas bg="#ffe9a8">
        {/* sunburst */}
        <g opacity={0.35} transform={`translate(960 330) rotate(${f * 0.3})`}>
          {Array.from({ length: 16 }).map((_, i) => (
            <path key={i} d="M0 0 L-90 -1400 L90 -1400 Z" fill="#ffd45e" transform={`rotate(${i * 22.5})`} />
          ))}
        </g>
        <Ground y={900} color="#f6d98a" />
        <Stick
          x={x}
          y={900}
          h={320}
          color={COLORS.dave}
          look="cap"
          mood={f > 175 ? "smug" : "happy"}
          walk={walking ? f * 0.4 : 0}
          armR={wave}
          elbowR={f > 92 && f < 175 ? -30 : 0}
          blink={blinkAt(f)}
        />
      </Canvas>
      <Stamp from={0} to={100} text="STORY TIME!" y={250} size={190} />
      <Stamp from={18} to={100} text="How I found out I was ADOPTED" y={430} size={88} color="white" rotate={2} />
      <Bubble from={100} to={180} x={960} y={570} dx={0} width={760} text="So... a few months ago, I found out I was adopted." />
      <Bubble from={182} to={270} x={960} y={570} dx={0} width={760} text="And honestly? The signs were there my WHOLE life." />
      <Sfx at={0} name="pop" />
      <Sfx at={18} name="pop" />
      <Sfx at={30} name="whoosh" volume={0.4} />
    </AbsoluteFill>
  );
};

/* ---------------- Scene 2: The height thing ---------------- */
const G2 = 880;
export const HeightScene: React.FC = () => {
  const f = useCurrentFrame();
  const pDad = usePop(10);
  const pMom = usePop(25);
  const pTara = usePop(40);
  const pDave = usePop(95, 7);
  const tDad = usePop(160);
  const tMom = usePop(190);
  const tTara = usePop(220);
  const tDave = usePop(290, 7);
  const tagsOn = f < 372 ? 1 : 0;
  const frozen = f >= 560;
  const bob = (o: number) => (frozen ? 0 : Math.sin((f + o) * 0.08) * 3);

  return (
    <AbsoluteFill>
      <Canvas bg="#e7f1f7">
        <Frame x={180} y={160} w={220} h={170} fill="#fff7d6">
          <circle cx={290} cy={245} r={40} fill="#ffcf3d" stroke={INK} strokeWidth={4} />
        </Frame>
        <Plant x={190} y={G2} />
        <Ground y={G2} color="#dccbb0" />
        <PopG x={560} y={G2} s={pDad}>
          <Stick x={560} y={G2 + bob(0)} h={600} color={COLORS.dad} look="dad" mood={frozen ? "neutral" : "happy"} armR={f > 470 && f < 560 ? 160 : 25} elbowR={f > 470 && f < 560 ? -40 : 0} blink={blinkAt(f, 20)} />
        </PopG>
        <PopG x={850} y={G2} s={pMom}>
          <Stick x={850} y={G2 + bob(10)} h={575} color={COLORS.mom} look="bun" mood={frozen ? "neutral" : "happy"} blink={blinkAt(f, 50)} />
        </PopG>
        <PopG x={1130} y={G2} s={pTara}>
          <Stick x={1130} y={G2 + bob(20)} h={550} color={COLORS.tara} look="pigtails" mood={frozen ? "smug" : "happy"} blink={blinkAt(f, 80)} />
        </PopG>
        <PopG x={1440} y={G2} s={pDave}>
          <Stick
            x={1440}
            y={G2 + bob(30)}
            h={280}
            color={COLORS.dave}
            look="cap"
            mood={f > 560 ? "neutral" : f > 380 ? "worried" : f > 290 ? "smug" : "happy"}
            armL={f > 290 && f < 380 ? 40 : 20}
            blink={blinkAt(f, 0)}
          />
        </PopG>
        <SvgTag x={560} y={G2 - 660} text={`6'8"`} s={tDad * tagsOn} color="#c9f0d6" />
        <SvgTag x={850} y={G2 - 635} text={`6'5"`} s={tMom * tagsOn} color="#ead6ff" />
        <SvgTag x={1130} y={G2 - 610} text={`6'3"`} s={tTara * tagsOn} color="#ffd6e6" />
        <SvgTag x={1440} y={G2 - 340} text={`5'2"`} s={tDave * tagsOn} color="#fff3a8" size={58} />
      </Canvas>
      <Chapter text="SIGN #1: THE HEIGHT THING" to={160} />
      <Caption from={10} to={150} text="My whole family is TALL. Like, 'duck under every doorway' tall." />
      <Caption from={152} to={282} text={`Dad is 6'8". Mom is 6'5". My sister Tara is 6'3".`} />
      <Caption from={284} to={375} text={`And me? 5'2". On a good day. In thick socks.`} />
      <Bubble from={380} to={468} x={1440} y={590} dx={0} width={480} text="Dad... why am I so short?" />
      <Bubble from={470} to={558} x={560} y={270} dx={260} width={620} text="You're just a late bloomer, son!" />
      <Stamp from={562} text="I WAS 34." x={1540} y={330} size={150} color="#ff8a7a" />
      <Sfx at={10} name="pop" />
      <Sfx at={25} name="pop" />
      <Sfx at={40} name="pop" />
      <Sfx at={95} name="boing" />
      <Sfx at={160} name="pop" volume={0.4} />
      <Sfx at={190} name="pop" volume={0.4} />
      <Sfx at={220} name="pop" volume={0.4} />
      <Sfx at={290} name="boing" />
      <Sfx at={560} name="scratch" volume={0.7} />
    </AbsoluteFill>
  );
};

/** Helper used by later scenes: linear fade. */
export const fade = (f: number, a: number, b: number) => interpolate(f, [a, b], [0, 1], { extrapolateLeft: "clamp", extrapolateRight: "clamp" });
