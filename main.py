import os
import discord

TOKEN = os.getenv("TOKEN")
VOICE_CHANNEL_ID = 1478399683385626799

intents = discord.Intents.default()
intents.voice_states = True
intents.guilds = True

client = discord.Client(intents=intents)

@client.event
async def on_ready():
    print(f"{client.user} aktif!")

    channel = client.get_channel(VOICE_CHANNEL_ID)

    if isinstance(channel, discord.VoiceChannel):
        try:
            await channel.connect(reconnect=True)
            print("Ses kanalına bağlandı!")
        except Exception as e:
            print("HATA:", e)
    else:
        print("Kanal bulunamadı.")

client.run(TOKEN)
