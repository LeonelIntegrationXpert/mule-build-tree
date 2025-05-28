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
    cmd = [sys.executable, str(script_path)] + (args or [])
    try:
        subprocess.run(cmd, check=True)
    except subprocess.CalledProcessError as e:
        print(f"\n❌ Erro ao executar {script_path.name}: (code {e.returncode})")
    pause()


def menu():
    base = Path(__file__).parent.resolve()
    build_dir = base / 'build'
    options = {
        '1': ('Universal Build Tool',      build_dir / 'run.py',      None),
        '2': ('Batch Deploy All',          build_dir / 'run-all.py',  None),
        '3': ('Auto Git Commit',           build_dir / 'auto_commit.py', None),
        '4': ('Gerar BOM pom.xml (bom.py)', build_dir / 'bom.py',      None),
        '0': ('Sair',                      None,                      None)
    }

    while True:
        clear_screen()
        print("╔═══════ Mule Build Tree – Ferramentas ═══════╗")
        for key, (desc, _, _) in options.items():
            print(f"║ [{key}] {desc.ljust(36)}║")
        print("╚═════════════════════════════════════════════╝")
        choice = input("Escolha uma opção: ").strip()

        if choice not in options:
            print("Opção inválida!")
            pause()
            continue

        desc, script, _ = options[choice]
        if choice == '0':
            print("Saindo...")
            sys.exit(0)

        # Chama o script selecionado
        print(f"\n👉 Executando: {desc}\n")
        if script.name == 'bom.py':
            tpl = input("Caminho do TEMPLATE [padrão ../mule-parent-bom-main/bom/pom.xml.template]: ") or "../mule-parent-bom-main/bom/pom.xml.template"
            vers = input("Caminho do JSON versões [padrão versions-bom.json]: ") or "versions-bom.json"
            out = input("Caminho de SAÍDA [padrão ../mule-parent-bom-main/bom/pom.xml]: ") or "../mule-parent-bom-main/bom/pom.xml"
            run_script(script, ['--template', tpl, '--versions', vers, '--output', out])
        else:
            run_script(script)


if __name__ == '__main__':
    menu()
