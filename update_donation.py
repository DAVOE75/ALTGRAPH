import os
import glob
import re

def update_donation_links(filepath):
    if not os.path.exists(filepath):
        return
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Replace PayPal URL
    content = content.replace(
        '<a href="https://www.paypal.me/latiendadeajedrez" target="_blank">',
        '<a href="https://buymeacoffee.com/hesiox" target="_blank">'
    )

    # Replace Spanish image shield
    content = content.replace(
        '<img src="https://img.shields.io/badge/☕_Invítame_a_un_café-0070BA?style=for-the-badge&logo=paypal&logoColor=white" alt="Invítame a un café" />',
        '<img src="https://img.shields.io/badge/☕_Invítame_a_un_café-FFDD00?style=for-the-badge&logo=buymeacoffee&logoColor=black" alt="Invítame a un café" />'
    )

    # Replace English image shield
    content = content.replace(
        '<img src="https://img.shields.io/badge/☕_Buy_me_a_coffee-0070BA?style=for-the-badge&logo=paypal&logoColor=white" alt="Buy me a coffee" />',
        '<img src="https://img.shields.io/badge/☕_Buy_me_a_coffee-FFDD00?style=for-the-badge&logo=buymeacoffee&logoColor=black" alt="Buy me a coffee" />'
    )
    
    # Replace French image shield (if any)
    content = content.replace(
        '<img src="https://img.shields.io/badge/☕_Offrez--moi_un_café-0070BA?style=for-the-badge&logo=paypal&logoColor=white"',
        '<img src="https://img.shields.io/badge/☕_Offrez--moi_un_café-FFDD00?style=for-the-badge&logo=buymeacoffee&logoColor=black"'
    )
    # Replace Italian
    content = content.replace(
        '<img src="https://img.shields.io/badge/☕_Offrimi_un_caffè-0070BA?style=for-the-badge&logo=paypal&logoColor=white"',
        '<img src="https://img.shields.io/badge/☕_Offrimi_un_caffè-FFDD00?style=for-the-badge&logo=buymeacoffee&logoColor=black"'
    )
    # Replace German
    content = content.replace(
        '<img src="https://img.shields.io/badge/☕_Spendier_mir_einen_Kaffee-0070BA?style=for-the-badge&logo=paypal&logoColor=white"',
        '<img src="https://img.shields.io/badge/☕_Spendier_mir_einen_Kaffee-FFDD00?style=for-the-badge&logo=buymeacoffee&logoColor=black"'
    )
    # Replace Portuguese
    content = content.replace(
        '<img src="https://img.shields.io/badge/☕_Paga--me_um_café-0070BA?style=for-the-badge&logo=paypal&logoColor=white"',
        '<img src="https://img.shields.io/badge/☕_Paga--me_um_café-FFDD00?style=for-the-badge&logo=buymeacoffee&logoColor=black"'
    )

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Updated {filepath}")

# Find all md files
files = glob.glob("c:/ALTGRAPH/*.md") + glob.glob("c:/ALTGRAPH/KarooTopoMaps/*.md")

for f in files:
    update_donation_links(f)
