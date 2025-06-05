import os
import subprocess
import datetime
import re

DEFAULT_NAME = "Leonel Dorneles Porto"
DEFAULT_EMAIL = "leoneldornelesporto@outlook.com.br"

def run_command(command):
    result = subprocess.run(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    return result.stdout.strip()

def get_git_changes():
    result = run_command(["git", "status", "--porcelain"])
    lines = result.split('\n')

    changes = []
    for line in lines:
        if not line.strip():
            continue

        match = re.match(r"^(\S+)\s+(.*)$", line.strip())
        if match:
            status, file_path = match.groups()
            changes.append(f"[{status}] {file_path}")
        else:
            changes.append(f"[?] {line.strip()}")
    return changes

def ensure_git_user_info():
    name = run_command(["git", "config", "user.name"])
    email = run_command(["git", "config", "user.email"])

    if not name:
        subprocess.run(["git", "config", "--local", "user.name", DEFAULT_NAME], check=True)
        name = DEFAULT_NAME
    if not email:
        subprocess.run(["git", "config", "--local", "user.email", DEFAULT_EMAIL], check=True)
        email = DEFAULT_EMAIL

    return name, email

def get_current_branch():
    return run_command(["git", "rev-parse", "--abbrev-ref", "HEAD"])

def auto_commit():
    # 🔄 Muda para a raiz do repositório
    repo_root = run_command(["git", "rev-parse", "--show-toplevel"])
    os.chdir(repo_root)

    print("🔍 Verificando alterações no repositório Git...\n")
    changes = get_git_changes()

    if not changes:
        print("✅ Nenhuma alteração detectada para commit.\n")
        return

    username, email = ensure_git_user_info()
    branch = get_current_branch()
    timestamp = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    subprocess.run(["git", "add", "."], check=True)

    # Construção do commit message profissional e detalhado
    commit_header = f"🤖 [Auto-Commit] Atualização automática em {timestamp}"
    commit_author = f"👤 Autor: {username} <{email}>"
    commit_branch = f"🌿 Branch: {branch}"
    commit_summary = f"📦 Arquivos alterados ({len(changes)}):"

    # Listagem detalhada, indentada para legibilidade
    commit_files = "\n".join(f"    • {change}" for change in changes)

    commit_message = f"""{commit_header}

{commit_author}
{commit_branch}

{commit_summary}
{commit_files}

🛠️ Todas as alterações foram automaticamente adicionadas, commitadas e enviadas para o repositório remoto.
🔒 Mensagem gerada automaticamente para rastreabilidade e auditoria.
"""

    # Executa o commit
    subprocess.run(["git", "commit", "-m", commit_message], check=True)

    print("\n🚀 Enviando commit para o repositório remoto...\n")
    subprocess.run(["git", "push", "origin", branch], check=True)

    print("✅ Commit e push concluídos com sucesso!")
    print("🔒 Histórico atualizado e salvo.\n")

if __name__ == "__main__":
    auto_commit()
