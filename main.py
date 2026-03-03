import os
import discord
from discord.ext import commands, tasks
from discord import app_commands

TOKEN = os.getenv("TOKEN")

# Kanal ve rol ID'leri
VOICE_CHANNEL_ID = 1478399683385626799  # Ses kanal ID
LOG_CHANNEL_ID = 123456789012345678    # Mod log kanalı ID
TICKET_CATEGORY_ID = 987654321098765432  # Ticket kategori ID
MOD_ROLE_ID = 112233445566778899        # Mod rol ID

intents = discord.Intents.default()
intents.members = True
intents.message_content = True
intents.voice_states = True

bot = commands.Bot(command_prefix="!", intents=intents)

# --- Ses Kanalına Bağlan ---
async def connect_to_voice():
    channel = bot.get_channel(VOICE_CHANNEL_ID)
    if channel and isinstance(channel, discord.VoiceChannel):
        if not channel.guild.voice_client:
            try:
                await channel.connect(reconnect=True)
                print("Ses kanalına bağlandı!")
            except Exception as e:
                print("Bağlanamadı:", e)

@bot.event
async def on_ready():
    print(f"{bot.user} aktif!")
    try:
        await bot.tree.sync()
    except Exception as e:
        print("Slash komut sync hatası:", e)
    await connect_to_voice()
    auto_reconnect.start()

@tasks.loop(seconds=30)
async def auto_reconnect():
    await connect_to_voice()

@bot.event
async def on_voice_state_update(member, before, after):
    if member.id == bot.user.id and after.channel is None:
        await connect_to_voice()

# --- Mod Log ---
async def send_mod_log(message):
    channel = bot.get_channel(LOG_CHANNEL_ID)
    if channel:
        await channel.send(message)

# --- Slash Komutlar ---

# Kick
@bot.tree.command(name="kick", description="Üyeyi sunucudan atar")
@app_commands.checks.has_permissions(kick_members=True)
async def kick(interaction: discord.Interaction, member: discord.Member):
    await member.kick()
    await interaction.response.send_message(f"{member.mention} atıldı.")
    await send_mod_log(f"🟠 {member} atıldı by {interaction.user}")

# Ban
@bot.tree.command(name="ban", description="Üyeyi sunucudan yasaklar")
@app_commands.checks.has_permissions(ban_members=True)
async def ban(interaction: discord.Interaction, member: discord.Member):
    await member.ban()
    await interaction.response.send_message(f"{member.mention} yasaklandı.")
    await send_mod_log(f"🔴 {member} yasaklandı by {interaction.user}")

# Clear
@bot.tree.command(name="clear", description="Mesaj siler")
@app_commands.checks.has_permissions(manage_messages=True)
async def clear(interaction: discord.Interaction, amount: int):
    await interaction.response.defer(ephemeral=True)
    await interaction.channel.purge(limit=amount)
    await interaction.followup.send(f"{amount} mesaj silindi", ephemeral=True)
    await send_mod_log(f"🟡 {amount} mesaj silindi by {interaction.user}")

# Mute
@bot.tree.command(name="mute", description="Üyeyi susturur")
@app_commands.checks.has_permissions(moderate_members=True)
async def mute(interaction: discord.Interaction, member: discord.Member, time: int = 5):
    mute_role = discord.utils.get(interaction.guild.roles, name="Muted")
    if not mute_role:
        mute_role = await interaction.guild.create_role(name="Muted")
    await member.add_roles(mute_role)
    await interaction.response.send_message(f"{member.mention} {time} dakika susturuldu")
    await send_mod_log(f"🔇 {member} susturuldu by {interaction.user} ({time} dakika)")

# Ticket sistemi
@bot.tree.command(name="ticket", description="Destek talebi oluşturur")
async def ticket(interaction: discord.Interaction, konu: str):
    category = bot.get_channel(TICKET_CATEGORY_ID)
    if category:
        overwrites = {
            interaction.guild.default_role: discord.PermissionOverwrite(view_channel=False),
            interaction.user: discord.PermissionOverwrite(view_channel=True)
        }
        channel = await category.create_text_channel(f"ticket-{interaction.user.name}", overwrites=overwrites)
        await channel.send(f"{interaction.user.mention} destek talebi açtı: **{konu}**")
        await interaction.response.send_message(f"Ticket oluşturuldu: {channel.mention}", ephemeral=True)
        await send_mod_log(f"🎫 Ticket açıldı: {interaction.user} -> {channel.name}")

# Yetki hataları
@kick.error
@ban.error
@clear.error
@mute.error
async def perms_error(interaction: discord.Interaction, error):
    if isinstance(error, app_commands.errors.MissingPermissions):
        await interaction.response.send_message("Yetkin yok.", ephemeral=True)

bot.run(TOKEN)
