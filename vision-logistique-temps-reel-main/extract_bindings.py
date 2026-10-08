import re

html = open('norival-frontend/src/app/app.component.html', encoding='utf-8').read()
props = set(re.findall(r'\[\(\w+?\)\]=\"([a-zA-Z0-9_.]+)\"', html))
props.update(re.findall(r'\{\{\s*([a-zA-Z0-9_.]+)\s*\}\}', html))
props.update(re.findall(r'\*ngIf=\"([a-zA-Z0-9_.!=<>\s\']+)\"', html))
lists = re.findall(r'let\s+(\w+)\s+of\s+([a-zA-Z0-9_$]+)', html)
methods = set(re.findall(r'\(click\)=\"([a-zA-Z0-9_]+)\(', html))
methods.update(re.findall(r'\(submit\)=\"([a-zA-Z0-9_]+)\(', html))
methods.update(re.findall(r'\(change\)=\"([a-zA-Z0-9_]+)\(', html))

print('Props:', props)
print('Lists:', set([l[1] for l in lists]))
print('Methods:', methods)
