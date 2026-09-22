from deep_translator import GoogleTranslator
import time

def translate_markdown(file_in, file_out):
    with open(file_in, 'r', encoding='utf-8') as f:
        lines = f.readlines()
        
    translator = GoogleTranslator(source='en', target='pt')
    
    with open(file_out, 'w', encoding='utf-8') as out:
        batch = ""
        for line in lines:
            if line.strip() == "" or line.startswith("!") or line.startswith("<img") or line.startswith("| <img"):
                out.write(line)
                continue
            
            # Simple chunking by line to avoid limits and formatting break
            try:
                translated = translator.translate(line)
                if translated:
                    out.write(translated + "\n")
                else:
                    out.write(line)
            except Exception as e:
                print(f"Error translating: {line}")
                out.write(line)
            time.sleep(0.05)
            
translate_markdown('README_en.md', 'README_pt.md')
