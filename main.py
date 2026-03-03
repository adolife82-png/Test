import os
import discord
from discord.ext import commands, tasks
from discord import app_commands

TOKEN = os.getenv("TOKEN")
VOICE_CHANNEL_ID = 1478399683385626799  # Ses kanal ID

intents = discord.Intents.default()
intents.members = True
intents.message_content = True
intents.voice_states = True

bot = commands.Bot(command_prefix="!", intents=intents)


async def connect_to_voice():
    channel = bot.get_channel(VOICE_CHANNEL_ID)

    if not channel:
        print("Ses kanalı bulunamadı.")
        return

    if not isinstance(channel, discord.VoiceChannel):
        print("ID bir ses kanalı değil.")
        return

    if not channel.guild.voice_client:
        try:
            await channel.connect()
            print("Ses kanalına bağlandı.")
        except Exception as e:
            print("Bağlantı hatası:", e)


@bot.event
async def on_ready():
    print(f"{bot.user} aktif!")

    try:
        await bot.tree.sync()
    except Exception as e:
        print("Slash sync hatası:", e)

    await connect_to_voice()
    auto_reconnect.start()


# 🔁 Sürekli Kontrol (30 saniyede bir)
@tasks.loop(seconds=30)
async def auto_reconnect():
    await connect_to_voice()


# 🎤 Eğer bot atılırsa geri girsin
@bot.event
async def on_voice_state_update(member, before, after):
    if member.id == bot.user.id and after.channel is None:
        await connect_to_voice()


# --- SLASH KOMUTLAR ---

@bot.tree.command(name="kick", description="Bir üyeyi sunucudan atar.")
@app_commands.checks.has_permissions(kick_members=True)
async def kick(interaction: discord.Interaction, member: discord.Member):
    await member.kick()
    await interaction.response.send_message(f"{member.mention} atıldı.")


@bot.tree.command(name="ban", description="Bir üyeyi yasaklar.")
@app_commands.checks.has_permissions(ban_members=True)
async def ban(interaction: discord.Interaction, member: discord.Member):
    await member.ban()
    await interaction.response.send_message(f"{member.mention} yasaklandı.")


@bot.tree.command(name="clear", description="Mesaj siler.")
@app_commands.checks.has_permissions(manage_messages=True)
async def clear(interaction: discord.Interaction, amount: int):
    await interaction.response.defer(ephemeral=True)
    await interaction.channel.purge(limit=amount)
    await interaction.followup.send(f"{amount} mesaj silindi.", ephemeral=True)


bot.run(TOKEN)
