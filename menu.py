#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Menu interativo para chamar as ferramentas de build, deploy e utilitários
no workspace Mule Build Tree.
"""
import os
import sys
import subprocess
from pathlib import Path

def clear_screen():
    os.system('cls' if os.name == 'nt' else 'clear')

def pause():
    input("\nPressione Enter para retornar ao menu...")

def run_script(script_path: Path, args=None):
    """
    Executa um script Python definindo o diretório de trabalho como a pasta do script,
    para que caminhos relativos funcionem corretamente.
    """
    cmd = [sys.executable, str(script_path)] + (args or [])
    try:
        subprocess.run(cmd, check=True, cwd=script_path.parent)
    except subprocess.CalledProcessError as e:
        print(f"\n❌ Erro ao executar {script_path.name}: (code {e.returncode})")
    pause()

def menu():
    base = Path(__file__).parent.resolve()
    build_dir = base / '1 - build'
    options = {
        '1': ('Universal Build Tool',       build_dir / 'run.py'),
        '2': ('Batch Deploy All',           build_dir / 'run-all.py'),
        '3': ('Auto Git Commit',            build_dir / 'auto_commit.py'),
        '4': ('Executar bom-parent.py',     build_dir / 'bom-parent.py'),
        '0': ('Sair',                        None)
    }

    # Largura interna do menu
    WIDTH = 59
    border_top = "╔" + "═" * WIDTH + "╗"
    border_mid = "╠" + "═" * WIDTH + "╣"
    border_bottom = "╚" + "═" * WIDTH + "╝"

    title = "Mule Build Tree – Ferramentas de Build 🚀"

    while True:
        clear_screen()
        print(border_top)
        print("║" + title.center(WIDTH) + "║")
        print(border_mid)
        for key, (desc, _) in options.items():
            line = f"[{key}] {desc}"
            print("║  " + line.ljust(WIDTH - 2) + "║")
        print(border_mid)
        info = "Selecione a ação desejada e pressione Enter"
        print("║" + info.center(WIDTH) + "║")
        print(border_bottom)

        choice = input("\n👉 Opção: ").strip()
        if choice not in options:
            print("\n❌ Opção inválida! Tente novamente.")
            pause()
            continue

        desc, script = options[choice]
        if choice == '0':
            print("\n👋 Saindo do Mule Build Tree. Até mais!")
            sys.exit(0)

        print(f"\n🚀 Executando: {desc}...\n")
        run_script(script)

if __name__ == '__main__':
    menu()