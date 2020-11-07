var fs = require('fs');

const SOURCE = fs.readFileSync(process.argv[2], "utf8");

const svelte = require('svelte/compiler');


// ############### DOM ##############

function dom(source,props={},charset='utf8'){
    const result = svelte.compile(source, {
        format: "cjs",
        generate: "dom"
    });1
    
    return result.js.code;
}

const PATTERN_IMPORT = /const\s\{?\s*\n*([A-z_$][A-z0-9_$]*\s*\n*,*\s*\n*?)*\s*\n*\}*\s*\n*=\s*\n*require\(\s*\n*[\"'][A-z0-9_$\\/@\\.,;:|-]*[\"'\\s*\\n*]\s*\n*\);?/gs;
const PATTERN_ITEMS = /(?<=const).*(?=\=)/gs;
const PATTERN_PATH = /(?<=require\()\s*\n*[\"'].*[\"']\s*\n*\);?/gs;
const RESULT = ""+dom(SOURCE,JSON.parse(process.argv[3]));
const IMPORTS = RESULT.match(PATTERN_IMPORT);

const {
    is_function,
    flush,
    add_render_callback,
    mount_component,
    run,
    run_all,
    get_current_component,
    blank_object,
    set_current_component,
    schedule_update,
} = require('svelte/internal');

let imports = [
    is_function.toString(),
    flush.toString(),
    add_render_callback.toString(),
    mount_component.toString(),
    run.toString(),
    run_all.toString(),
    get_current_component.toString(),
    blank_object.toString(),
    set_current_component.toString(),
    schedule_update.toString(),
].join("\n")+"\n";

IMPORTS.forEach(importStr=>{
    const ITEMS = importStr.match(PATTERN_ITEMS)[0].trim().replace(/{|}/gs,"").trim().split(",");
    ITEMS.forEach(item=>{
        item = item.trim();
        importStr += `imports += ${item}.toString();`;
    });
    eval(importStr);
});

const SCRIPT = imports+"\n"+RESULT.replace(PATTERN_IMPORT,'');



// ############### SSR ##############


function ssr(source,props={},charset='utf8'){
    const result = svelte.compile(source, {
        format: "cjs",
        generate: "ssr"
    });
    const item = eval(result.js.code);
    return item.render(props);
}

const result = ssr(SOURCE,JSON.parse(process.argv[3]));

console.log(
`<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${process.argv[4]}</title>
    <style>${result.css.code}</style>
    <script deffer type='module'>
        const App = (function(){
            const exports = {};

            let flushing = false;
            const seen_callbacks = new Set();
            const dirty_components = [];
            const intros = { enabled: false };
            const binding_callbacks = [];
            const render_callbacks = [];
            const flush_callbacks = [];
            const resolved_promise = Promise.resolve();
            let update_scheduled = false;
            function make_dirty(component, i) {
                if (component.$$.dirty[0] === -1) {
                    dirty_components.push(component);
                    schedule_update();
                    component.$$.dirty.fill(0);
                }
                component.$$.dirty[(i / 31) | 0] |= (1 << (i % 31));
            }
            function update($$) {
                if ($$.fragment !== null) {
                    $$.update();
                    run_all($$.before_update);
                    const dirty = $$.dirty;
                    $$.dirty = [-1];
                    $$.fragment && $$.fragment.p($$.ctx, dirty);
                    $$.after_update.forEach(add_render_callback);
                }
            }

            ${SCRIPT}
            return exports.default;
        })();
        document.body.innerHTML = '';
        new App({
            target: document.body,
            props: ${process.argv[3]}
        });
    </script>
    ${result.head}
</head>
<body>
    ${result.html}
</body>
</html>`
);