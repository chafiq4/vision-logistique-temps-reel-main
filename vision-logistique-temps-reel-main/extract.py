import json
import os

transcript_path = r'C:\Users\pc\.gemini\antigravity-ide\brain\796b93b8-2bc0-40ab-aa7c-ef41d27ad47b\.system_generated\logs\transcript.jsonl'
output_path = r'C:\Users\pc\Desktop\stage_4iir\extracted_app.ts'

content_to_save = None

with open(transcript_path, 'r', encoding='utf-8') as f:
    for line in f:
        try:
            step = json.loads(line)
            if 'tool_calls' in step:
                for call in step['tool_calls']:
                    # sometimes the tool responses are in the same step, sometimes in the next
                    pass
            if step.get('type') == 'ACTION_OUTPUT':
                out_content = step.get('content', '')
                if 'app.component.ts' in out_content and 'Showing lines 1 to' in out_content:
                    content_to_save = out_content
        except Exception as e:
            pass

if content_to_save:
    print('Found view_file output. Length:', len(content_to_save))
    with open(output_path, 'w', encoding='utf-8') as out_f:
        out_f.write(content_to_save)
else:
    print('Not found')
