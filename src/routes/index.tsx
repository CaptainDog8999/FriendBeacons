import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/")({ component: Home });

const PAPER_JAR =
  "https://github.com/CaptainDog8999/FriendBeacons/raw/main/attachments/FriendBeacons-paper-26.2.jar";
const FABRIC_JAR =
  "https://github.com/CaptainDog8999/FriendBeacons/raw/main/attachments/FriendBeacons-fabric-26.2.jar";

const commands = [
  { cmd: "/friendbeacons share <player>", effect: "That player may see your live pin" },
  { cmd: "/friendbeacons unshare <player>", effect: "Revoke their view" },
  { cmd: "/friendbeacons everyone", effect: "Anyone online with the client + Xaero's can see you" },
  { cmd: "/friendbeacons nobody", effect: "Hide your pin (default)" },
  { cmd: "/friendbeacons list", effect: "Who can see you" },
  { cmd: "/friendbeacons incoming", effect: "Who is sharing with you" },
  { cmd: "/friendbeacons color <color>", effect: "Change pin color" },
  { cmd: "/friendbeacons", effect: "Status" },
];

function Pin() {
  return (
    <svg viewBox="0 0 24 24" className="size-7 fill-[#9aaa90]" aria-hidden="true">
      <path d="M12 2a7 7 0 0 0-7 7c0 5.25 7 13 7 13s7-7.75 7-13a7 7 0 0 0-7-7zm0 9.5A2.5 2.5 0 1 1 12 6a2.5 2.5 0 0 1 0 5.5z" />
    </svg>
  );
}

function Home() {
  return (
    <div className="min-h-screen bg-[#121212] text-[#e6dcc8]">
      <header
        className="border-b border-white/10 bg-cover bg-center"
        style={{ backgroundImage: "url(/x-banner.jpg)" }}
      >
        <div className="bg-black/45">
          <div className="mx-auto flex max-w-5xl items-center justify-between px-6 py-5">
            <div className="flex items-center gap-3">
              <Pin />
              <span className="fb-serif text-sm text-[#c5d4b8]">FRIEND BEACONS</span>
            </div>
            <a
              href="#download"
              className="rounded-full border border-[#9aaa90]/50 px-4 py-2 text-xs font-semibold tracking-wide text-[#c5d4b8] hover:border-[#c5d4b8]"
            >
              DOWNLOAD
            </a>
          </div>
        </div>
      </header>

      <section
        className="relative overflow-hidden bg-cover bg-center"
        style={{ backgroundImage: "url(/og.jpg)" }}
      >
        <div className="absolute inset-0 bg-black/55" />
        <div className="relative mx-auto max-w-5xl px-6 py-24 text-center sm:py-32">
          <Pin />
          <h1 className="fb-serif mt-6 text-4xl text-[#e6dcc8] sm:text-6xl">FRIEND BEACONS</h1>
          <div className="mx-auto mt-4 h-px w-24 bg-[#9aaa90]/70" />
          <p className="mx-auto mt-6 max-w-xl text-base text-[#c5d4b8]/90 sm:text-lg">
            Live minimap pins for friends who opt in. Minecraft 26.2.
          </p>
          <p className="fb-serif mt-4 text-xs tracking-[0.22em] text-[#9aaa90]">
            A MOD MADE BY SHARD
          </p>
        </div>
      </section>

      <main className="mx-auto max-w-5xl px-6 py-16">
        <div className="grid gap-6 sm:grid-cols-3">
          {[
            {
              title: "Opt-in only",
              body: "Nobody sees you until you run /friendbeacons share or /friendbeacons everyone. Default is hidden.",
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
            <article key={item.title} className="rounded-xl border border-[#9aaa90]/20 bg-black/40 p-6">
              <h2 className="fb-serif text-sm text-[#c5d4b8]">{item.title.toUpperCase()}</h2>
              <p className="mt-3 text-sm leading-6 text-[#e6dcc8]/75">{item.body}</p>
            </article>
          ))}
        </div>

        <section className="mt-16" id="download">
          <h2 className="fb-serif text-xl text-[#c5d4b8]">WHO NEEDS WHAT</h2>
          <div className="mt-6 overflow-x-auto rounded-xl border border-[#9aaa90]/20">
            <table className="w-full min-w-[36rem] text-left text-sm">
              <thead className="bg-black/50 text-[#c5d4b8]">
                <tr>
                  <th className="px-4 py-3 font-medium">Piece</th>
                  <th className="px-4 py-3 font-medium">Where it goes</th>
                  <th className="px-4 py-3 font-medium">Needed to</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#9aaa90]/15 text-[#e6dcc8]/75">
                <tr>
                  <td className="px-4 py-3 text-[#e6dcc8]">Paper jar</td>
                  <td className="px-4 py-3">Paper server plugins/</td>
                  <td className="px-4 py-3">Run commands and broadcast pins</td>
                </tr>
                <tr>
                  <td className="px-4 py-3 text-[#e6dcc8]">Fabric jar</td>
                  <td className="px-4 py-3">Fabric server or client mods/</td>
                  <td className="px-4 py-3">Server broadcast or client pins</td>
                </tr>
                <tr>
                  <td className="px-4 py-3 text-[#e6dcc8]">Xaero's Minimap 26.2</td>
                  <td className="px-4 py-3">Viewing client only</td>
                  <td className="px-4 py-3">Draw the waypoint</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div className="mt-6 flex flex-wrap gap-3">
            <a
              href={PAPER_JAR}
              className="rounded-full bg-[#9aaa90] px-5 py-3 text-sm font-semibold text-[#121212] hover:bg-[#c5d4b8]"
            >
              Download Paper 26.2
            </a>
            <a
              href={FABRIC_JAR}
              className="rounded-full border border-[#9aaa90]/50 px-5 py-3 text-sm font-semibold text-[#c5d4b8] hover:border-[#c5d4b8]"
            >
              Download Fabric 26.2
            </a>
          </div>
        </section>

        <section className="mt-16 pb-8" id="commands">
          <h2 className="fb-serif text-xl text-[#c5d4b8]">COMMANDS</h2>
          <ul className="mt-6 divide-y divide-[#9aaa90]/15 overflow-hidden rounded-xl border border-[#9aaa90]/20">
            {commands.map((row) => (
              <li
                key={row.cmd}
                className="grid gap-1 px-4 py-3 sm:grid-cols-[20rem_1fr] sm:items-center"
              >
                <code className="font-mono text-sm text-[#c5d4b8]">{row.cmd}</code>
                <span className="text-sm text-[#e6dcc8]/70">{row.effect}</span>
              </li>
            ))}
          </ul>
        </section>
      </main>

      <footer className="border-t border-[#9aaa90]/15 py-8 text-center text-sm text-[#9aaa90]">
        Friend Beacons is a Minecraft mod made by Shard.
      </footer>
    </div>
  );
}
