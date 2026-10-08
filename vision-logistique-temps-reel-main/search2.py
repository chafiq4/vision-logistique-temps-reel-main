import json

path = r'C:\Users\pc\.gemini\antigravity-ide\brain\796b93b8-2bc0-40ab-aa7c-ef41d27ad47b\.system_generated\logs\transcript.jsonl'
output = r'C:\Users\pc\Desktop\stage_4iir\found_views2.txt'

with open(path, 'r', encoding='utf-8') as f, open(output, 'w', encoding='utf-8') as out:
    for line in f:
        if 'app.component.ts' in line and 'Showing lines' in line:
            data = json.loads(line)
            content = data.get('content', '')
            lines_header = [l for l in content.split('\n') if 'Showing lines' in l]
            out.write(str(lines_header) + '\n')
