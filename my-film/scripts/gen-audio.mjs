// Generates the background music and cartoon sound effects as WAV files
// in public/audio. Run with: node scripts/gen-audio.mjs
import { writeFileSync, mkdirSync } from "node:fs";
import { join } from "node:path";

const SR = 44100;
const OUT = join(import.meta.dirname, "..", "public", "audio");
mkdirSync(OUT, { recursive: true });

let seed = 7;
const rand = () => {
  seed = (seed * 16807) % 2147483647;
  return seed / 2147483647;
};
const noise = () => rand() * 2 - 1;

const buf = (sec) => new Float32Array(Math.ceil(sec * SR));
const midi = (n) => 440 * Math.pow(2, (n - 69) / 12);

function save(name, data, peak = 0.85) {
  let max = 0;
  for (const v of data) max = Math.max(max, Math.abs(v));
  const gain = max > 0 ? peak / max : 1;
  const out = Buffer.alloc(44 + data.length * 2);
  out.write("RIFF", 0);
  out.writeUInt32LE(36 + data.length * 2, 4);
  out.write("WAVE", 8);
  out.write("fmt ", 12);
  out.writeUInt32LE(16, 16);
  out.writeUInt16LE(1, 20);
  out.writeUInt16LE(1, 22);
  out.writeUInt32LE(SR, 24);
  out.writeUInt32LE(SR * 2, 28);
  out.writeUInt16LE(2, 32);
  out.writeUInt16LE(16, 34);
  out.write("data", 36);
  out.writeUInt32LE(data.length * 2, 40);
  for (let i = 0; i < data.length; i++) {
    const v = Math.max(-1, Math.min(1, data[i] * gain));
    out.writeInt16LE(Math.round(v * 32767), 44 + i * 2);
  }
  writeFileSync(join(OUT, name), out);
  console.log("wrote", name, (data.length / SR).toFixed(2) + "s");
}

// Adds one note to a buffer. wave: function of phase (0..1) -> -1..1
function note(b, start, dur, freq, vol, wave, { attack = 0.005, decay = 4, release = 0.03 } = {}) {
  const s = Math.floor(start * SR);
  const n = Math.floor((dur + release) * SR);
  let ph = 0;
  for (let i = 0; i < n && s + i < b.length; i++) {
    const t = i / SR;
    let env = Math.min(1, t / attack) * Math.exp(-decay * t);
    if (t > dur) env *= Math.max(0, 1 - (t - dur) / release);
    ph += freq / SR;
    b[s + i] += wave(ph % 1, t) * env * vol;
  }
}
const sine = (p) => Math.sin(2 * Math.PI * p);
const tri = (p) => 1 - 4 * Math.abs(Math.round(p - 0.25) - (p - 0.25));
const soft = (p) => sine(p) * 0.7 + sine(p * 2) * 0.2 + sine(p * 3) * 0.1;
const bell = (p) => sine(p) * 0.75 + sine(p * 2) * 0.18 + sine(p * 4.01) * 0.07;

// ---------- Main music: bouncy, light, 120 BPM, C-G-Am-F ----------
{
  const LEN = 124;
  const b = buf(LEN);
  const beat = 0.5;
  const prog = [
    [48, [60, 64, 67]],
    [43, [59, 62, 67]],
    [45, [60, 64, 69]],
    [41, [60, 65, 69]],
  ];
  const scale = [72, 74, 76, 79, 81, 84];
  for (let bar = 0; bar * 4 * beat < LEN; bar++) {
    const [root, chord] = prog[bar % 4];
    const t0 = bar * 4 * beat;
    // bass: root on 1 and 3, octave on 2 and 4
    for (let k = 0; k < 4; k++) {
      note(b, t0 + k * beat, 0.22, midi(root + (k % 2 ? 12 : 0)), 0.32, tri, { decay: 7 });
    }
    // off-beat chord stabs
    for (let k = 0; k < 4; k++) {
      for (const c of chord) {
        note(b, t0 + k * beat + beat / 2, 0.1, midi(c), 0.07, soft, { decay: 14 });
      }
    }
    // hi-hat ticks
    for (let k = 0; k < 8; k++) {
      const s = Math.floor((t0 + k * beat * 0.5) * SR);
      for (let i = 0; i < 1200 && s + i < b.length; i++) {
        b[s + i] += noise() * 0.035 * Math.exp(-i / 250) * (k % 2 ? 1 : 0.6);
      }
    }
    // playful melody, skip every 4th phrase so it breathes
    if (Math.floor(bar / 4) % 4 !== 3) {
      const pattern = bar % 2 === 0 ? [0, 1, 1.5, 2.5, 3] : [0, 0.5, 1, 2, 3.5];
      for (const p of pattern) {
        const n = scale[Math.floor(rand() * scale.length)];
        note(b, t0 + p * beat, 0.25, midi(n), 0.13, bell, { decay: 5 });
      }
    }
  }
  // fade in/out
  for (let i = 0; i < b.length; i++) {
    const t = i / SR;
    b[i] *= Math.min(1, t / 0.5) * Math.min(1, (LEN - t) / 3);
  }
  save("music.wav", b, 0.8);
}

// ---------- Soft music for the heartfelt part: slow arpeggios ----------
{
  const LEN = 16;
  const b = buf(LEN);
  const chords = [
    [48, 55, 60, 64, 67],
    [41, 48, 57, 60, 65],
    [45, 52, 57, 60, 64],
    [43, 50, 55, 59, 62],
  ];
  for (let c = 0; c * 4 < LEN; c++) {
    const ch = chords[c % 4];
    ch.forEach((n, k) => {
      note(b, c * 4 + k * 0.32, 3.2, midi(n), 0.16, bell, { decay: 1.1, attack: 0.01 });
    });
    note(b, c * 4 + 2, 1.6, midi(ch[4] + 12), 0.08, bell, { decay: 1.6 });
  }
  for (let i = 0; i < b.length; i++) {
    const t = i / SR;
    b[i] *= Math.min(1, t / 0.8) * Math.min(1, (LEN - t) / 2);
  }
  save("music-soft.wav", b, 0.75);
}

// ---------- Sound effects ----------
{
  // pop: quick upward blip
  const b = buf(0.15);
  let ph = 0;
  for (let i = 0; i < b.length; i++) {
    const t = i / SR;
    ph += (350 + 1400 * (t / 0.15)) / SR;
    b[i] = sine(ph) * Math.exp(-t * 30);
  }
  save("pop.wav", b);
}
{
  // boing: wobbly spring
  const b = buf(0.8);
  let ph = 0;
  for (let i = 0; i < b.length; i++) {
    const t = i / SR;
    const f = 180 + 160 * Math.min(1, t * 6) + 60 * Math.sin(t * 2 * Math.PI * 14) * Math.exp(-t * 3);
    ph += f / SR;
    b[i] = (sine(ph) * 0.8 + sine(ph * 2) * 0.2) * Math.exp(-t * 4) * Math.min(1, t * 200);
  }
  save("boing.wav", b);
}
{
  // record scratch
  const b = buf(0.55);
  let ph = 0;
  let lp = 0;
  for (let i = 0; i < b.length; i++) {
    const t = i / SR;
    const sweep = t < 0.18 ? 1 - t / 0.18 : (t - 0.18) / 0.37;
    const f = 120 + 900 * sweep;
    ph += f / SR;
    const saw = (ph % 1) * 2 - 1;
    lp += 0.25 * (noise() - lp);
    b[i] = (saw * 0.6 + (noise() - lp) * 0.5) * Math.exp(-t * 3) * Math.min(1, t * 300);
  }
  save("scratch.wav", b);
}
{
  // drum roll + crash
  const LEN = 3.6;
  const b = buf(LEN);
  const roll = 2.4;
  for (let k = 0; k * (1 / 22) < roll; k++) {
    const s = Math.floor((k / 22) * SR);
    const vol = 0.25 + 0.75 * (k / 22 / roll);
    let lp = 0;
    for (let i = 0; i < 2500 && s + i < b.length; i++) {
      lp += 0.35 * (noise() - lp);
      b[s + i] += lp * vol * Math.exp(-i / 500);
    }
  }
  const s = Math.floor(roll * SR);
  let lp = 0;
  for (let i = 0; s + i < b.length; i++) {
    const t = i / SR;
    const n = noise();
    lp += 0.5 * (n - lp);
    b[s + i] += (n - lp) * 1.4 * Math.exp(-t * 2.2) + sine((t * 70) % 1) * Math.exp(-t * 10);
  }
  save("drumroll.wav", b);
}
{
  // dramatic "dun dun DUNNN"
  const b = buf(2.6);
  const brass = (p, t) => {
    let v = 0;
    for (let h = 1; h <= 8; h++) v += sine(p * h) / h;
    return v * (1 + 0.15 * Math.sin(t * 2 * Math.PI * 6));
  };
  const notes = [
    [0, 0.28, 50],
    [0.38, 0.28, 51],
    [0.78, 1.6, 49],
  ];
  for (const [t, d, n] of notes) {
    note(b, t, d, midi(n), 0.4, brass, { decay: 0.6, attack: 0.02, release: 0.2 });
    note(b, t, d, midi(n - 12), 0.35, brass, { decay: 0.6, attack: 0.02, release: 0.2 });
  }
  save("dun.wav", b);
}
{
  // whoosh
  const b = buf(0.6);
  let lp = 0;
  for (let i = 0; i < b.length; i++) {
    const t = i / SR;
    const a = 0.02 + 0.25 * Math.sin(Math.PI * (t / 0.6));
    lp += a * (noise() - lp);
    b[i] = lp * Math.sin(Math.PI * (t / 0.6));
  }
  save("whoosh.wav", b);
}
{
  // ding
  const b = buf(1.4);
  note(b, 0, 1.3, 1318.5, 0.6, bell, { decay: 3 });
  note(b, 0, 1.3, 2637, 0.2, sine, { decay: 5 });
  save("ding.wav", b);
}
{
  // page flip
  const b = buf(0.25);
  let lp = 0;
  for (let i = 0; i < b.length; i++) {
    const t = i / SR;
    const n = noise();
    lp += 0.3 * (n - lp);
    b[i] = (n - lp) * Math.exp(-t * 18) * Math.min(1, t * 150);
  }
  save("page.wav", b);
}
{
  // slap on the table
  const b = buf(0.35);
  let ph = 0;
  for (let i = 0; i < b.length; i++) {
    const t = i / SR;
    ph += (110 - 60 * Math.min(1, t * 8)) / SR;
    b[i] = noise() * Math.exp(-t * 45) * 0.9 + sine(ph) * Math.exp(-t * 14);
  }
  save("slap.wav", b);
}
{
  // spit "ptoo"
  const b = buf(0.3);
  let ph = 0;
  for (let i = 0; i < b.length; i++) {
    const t = i / SR;
    ph += (500 - 1200 * t) / SR;
    b[i] = noise() * Math.exp(-t * 60) * 0.7 + sine(ph) * Math.exp(-t * 12) * 0.6;
  }
  save("spit.wav", b);
}
{
  // ta-da arpeggio
  const b = buf(1.8);
  [72, 76, 79, 84].forEach((n, k) => {
    note(b, k * 0.09, 1.2 - k * 0.09, midi(n), 0.35, soft, { decay: 2.5 });
  });
  note(b, 0.36, 1.3, midi(60), 0.3, tri, { decay: 2 });
  save("tada.wav", b);
}
{
  // gulp / thud for falling turkey
  const b = buf(0.4);
  let ph = 0;
  for (let i = 0; i < b.length; i++) {
    const t = i / SR;
    ph += (90 - 50 * t) / SR;
    b[i] = sine(ph) * Math.exp(-t * 12) + noise() * Math.exp(-t * 60) * 0.4;
  }
  save("thud.wav", b);
}
