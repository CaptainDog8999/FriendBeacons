import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/")({ component: Home });

const PAPER_JAR =
  "https://github.com/CaptainDog8999/FriendBeacons/raw/main/attachments/FriendBeacons-paper-26.2.jar";
const FABRIC_JAR =
  "https://github.com/CaptainDog8999/FriendBeacons/raw/main/attachments/FriendBeacons-fabric-26.2.jar";

const commands = [
  { cmd: "/fwp share <player>", effect: "That player may see your live pin" },
  { cmd: "/fwp unshare <player>", effect: "Revoke their view" },
  { cmd: "/fwp everyone", effect: "Anyone online with the client + Xaero's can see you" },
  { cmd: "/fwp nobody", effect: "Hide your pin (default)" },
  { cmd: "/fwp list", effect: "Who can see you" },
  { cmd: "/fwp incoming", effect: "Who is sharing with you" },
  { cmd: "/fwp color <color>", effect: "Change pin color" },
  { cmd: "/fwp", effect: "Status" },
];

function Home() {
  return (
    <div className="min-h-screen bg-zinc-950 text-zinc-50">
      <header className="border-b border-zinc-800">
        <div className="mx-auto flex max-w-5xl items-center justify-between px-6 py-4">
          <div className="flex items-center gap-3">
            <span className="grid size-9 place-items-center rounded-lg bg-amber-400 text-lg font-black text-zinc-950">
              FB
            </span>
            <span className="font-semibold tracking-tight">Friend Beacons</span>
          </div>
          <a
            href="#download"
            className="rounded-full bg-amber-400 px-4 py-2 text-sm font-semibold text-zinc-950 hover:bg-amber-300"
          >
            Download
          </a>
        </div>
      </header>

      <main>
        <section className="mx-auto max-w-5xl px-6 py-16 sm:py-24">
          <p className="text-sm font-medium uppercase tracking-[0.2em] text-amber-400">
            Minecraft 26.2
          </p>
          <h1 className="mt-4 max-w-3xl text-4xl font-semibold tracking-tight sm:text-6xl">
            Live minimap pins for friends who opt in.
          </h1>
          <p className="mt-6 max-w-2xl text-lg text-zinc-400">
            Share a beacon with people you choose. Paper, Fabric, and NeoForge
            speak the same packet format, so mixed servers still work. Pins only
            show if Xaero's Minimap is installed.
          </p>
          <div className="mt-8 flex flex-wrap gap-3">
            <a
              href="#download"
              className="rounded-full bg-amber-400 px-5 py-3 text-sm font-semibold text-zinc-950 hover:bg-amber-300"
            >
              Get the jars
            </a>
            <a
              href="#commands"
              className="rounded-full border border-zinc-700 px-5 py-3 text-sm font-semibold text-zinc-100 hover:border-zinc-500"
            >
              View commands
            </a>
          </div>
        </section>

        <section className="border-y border-zinc-800 bg-zinc-900/40">
          <div className="mx-auto grid max-w-5xl gap-6 px-6 py-14 sm:grid-cols-3">
            {[
              {
                title: "Opt-in only",
                body: "Nobody sees you until you run /fwp share or /fwp everyone. Default is hidden.",
              },
              {
                title: "Mixed loaders",
                body: "A Paper server can drive Fabric and NeoForge clients. One protocol, three jars.",
              },
              {
                title: "Xaero's pins",
                body: "The client jar plus Xaero's Minimap draws the waypoint. No Xaero's, no pin.",
              },
            ].map((item) => (
              <article key={item.title} className="rounded-2xl border border-zinc-800 bg-zinc-950 p-6">
                <h2 className="text-lg font-semibold">{item.title}</h2>
                <p className="mt-3 text-sm leading-6 text-zinc-400">{item.body}</p>
              </article>
            ))}
          </div>
        </section>

        <section className="mx-auto max-w-5xl px-6 py-16" id="download">
          <h2 className="text-3xl font-semibold tracking-tight">Who needs what</h2>
          <div className="mt-8 overflow-x-auto rounded-2xl border border-zinc-800">
            <table className="w-full min-w-[36rem] text-left text-sm">
              <thead className="bg-zinc-900 text-zinc-300">
                <tr>
                  <th className="px-4 py-3 font-medium">Piece</th>
                  <th className="px-4 py-3 font-medium">Where it goes</th>
                  <th className="px-4 py-3 font-medium">Needed to</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-zinc-800 text-zinc-400">
                <tr>
                  <td className="px-4 py-3 text-zinc-100">Paper jar</td>
                  <td className="px-4 py-3">Paper server plugins/</td>
                  <td className="px-4 py-3">Run commands and broadcast pins</td>
                </tr>
                <tr>
                  <td className="px-4 py-3 text-zinc-100">Fabric jar</td>
                  <td className="px-4 py-3">Fabric server or client mods/</td>
                  <td className="px-4 py-3">Server broadcast or client pins</td>
                </tr>
                <tr>
                  <td className="px-4 py-3 text-zinc-100">Xaero's Minimap 26.2</td>
                  <td className="px-4 py-3">Viewing client only</td>
                  <td className="px-4 py-3">Draw the waypoint</td>
                </tr>
              </tbody>
            </table>
          </div>

          <div className="mt-6 flex flex-wrap gap-3">
            <a
              href={PAPER_JAR}
              className="rounded-full bg-amber-400 px-5 py-3 text-sm font-semibold text-zinc-950 hover:bg-amber-300"
            >
              Download Paper 26.2
            </a>
            <a
              href={FABRIC_JAR}
              className="rounded-full border border-zinc-700 px-5 py-3 text-sm font-semibold text-zinc-100 hover:border-zinc-500"
            >
              Download Fabric 26.2
            </a>
          </div>
          <p className="mt-4 text-sm text-zinc-500">
            Do not mix Minecraft 1.21.1 with 26.2. Java 25. Sharing is one-way.
            Alias: /friendbeacons.
          </p>
        </section>

        <section className="mx-auto max-w-5xl px-6 pb-20" id="commands">
          <h2 className="text-3xl font-semibold tracking-tight">Commands</h2>
          <ul className="mt-8 divide-y divide-zinc-800 overflow-hidden rounded-2xl border border-zinc-800">
            {commands.map((row) => (
              <li key={row.cmd} className="grid gap-1 px-4 py-3 sm:grid-cols-[16rem_1fr] sm:items-center">
                <code className="font-mono text-sm text-amber-300">{row.cmd}</code>
                <span className="text-sm text-zinc-400">{row.effect}</span>
              </li>
            ))}
          </ul>
        </section>
      </main>
    </div>
  );
}
