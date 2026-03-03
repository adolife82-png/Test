import os
import discord
from discord import app_commands
from discord.ext import commands

TOKEN = os.getenv("TOKEN")

intents = discord.Intents.default()
intents.members = True
intents.message_content = True

bot = commands.Bot(command_prefix="!", intents=intents)

@bot.event
async def on_ready():
    await bot.tree.sync()
    print(f"{bot.user} aktif!")

# 🔨 Kick
@bot.tree.command(name="kick", description="Bir üyeyi sunucudan atar.")
@app_commands.checks.has_permissions(kick_members=True)
async def kick(interaction: discord.Interaction, member: discord.Member):
    await member.kick()
    await interaction.response.send_message(
        f"{member.mention} sunucudan atıldı.",
        ephemeral=False
    )

# 🔨 Ban
@bot.tree.command(name="ban", description="Bir üyeyi yasaklar.")
@app_commands.checks.has_permissions(ban_members=True)
async def ban(interaction: discord.Interaction, member: discord.Member):
    await member.ban()
    await interaction.response.send_message(
        f"{member.mention} yasaklandı.",
        ephemeral=False
    )

# 🗑️ Clear
@bot.tree.command(name="clear", description="Belirtilen sayıda mesaj siler.")
@app_commands.checks.has_permissions(manage_messages=True)
async def clear(interaction: discord.Interaction, amount: int):
    await interaction.response.defer(ephemeral=True)
    await interaction.channel.purge(limit=amount)
    await interaction.followup.send(
        f"{amount} mesaj silindi.",
        ephemeral=True
    )

# ❌ Yetki hatası
@kick.error
@ban.error
@clear.error
async def permissions_error(interaction: discord.Interaction, error):
    if isinstance(error, app_commands.errors.MissingPermissions):
        await interaction.response.send_message(
            "Bu komutu kullanmak için yetkin yok.",
            ephemeral=True
        )

bot.run(TOKEN)
