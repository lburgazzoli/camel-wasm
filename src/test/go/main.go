// go run main.go ../resources/to_upper.wasm '{ "headers": { "foo": "bar"}, "body": "aGVsbG8K" }'
// {"headers":{"foo":"bar"},"body":"SEVMTE8K"}

package main

import (
	"context"
	_ "embed"
	"fmt"
	"log"
	"os"

	"github.com/tetratelabs/wazero"
)

// main shows how to interact with a WebAssembly function that was compiled
// from Rust.
//
// See README.md for a full description.
func main() {
	ctx := context.Background()

	r := wazero.NewRuntime(ctx)
	defer func() {
		_ = r.Close(ctx)
	}()

	_, err := r.NewHostModuleBuilder("env").Instantiate(ctx)
	if err != nil {
		log.Panicln(err)
	}

	resource := os.Args[1]
	data := os.Args[2]
	dataSize := uint64(len(data))

	content, err := os.ReadFile(resource)
	if err != nil {
		log.Panicln(err)
	}

	mod, err := r.Instantiate(ctx, content)
	if err != nil {
		log.Panicln(err)
	}

	process := mod.ExportedFunction("process")
	allocate := mod.ExportedFunction("alloc")
	deallocate := mod.ExportedFunction("dealloc")

	results, err := allocate.Call(ctx, dataSize)
	if err != nil {
		log.Panicln(err)
	}

	dataPtr := results[0]

	defer func() {
		_, _ = deallocate.Call(ctx, dataPtr, dataSize)
	}()

	// The pointer is a linear memory offset, which is where we write the name.
	if !mod.Memory().Write(uint32(dataPtr), []byte(data)) {
		log.Panicf("Memory.Write(%d, %d) out of range of memory size %d", dataPtr, dataSize, mod.Memory().Size())
	}

	// Now, we can call "greet", which reads the string we wrote to memory!
	ptrSize, err := process.Call(ctx, dataPtr, dataSize)
	if err != nil {
		log.Panicln(err)
	}

	resultPtr := uint32(ptrSize[0] >> 32)
	resultSize := uint32(ptrSize[0])

    //fmt.Println(ptrSize[0])

    fmt.Println(dataPtr)
    fmt.Println(dataSize)

    fmt.Println(resultPtr)
    fmt.Println(resultSize)

	// This pointer was allocated by Rust, but owned by Go, So, we have to
	// deallocate it when finished
	defer func() {
		_, _ = deallocate.Call(ctx, uint64(resultPtr), uint64(resultSize))
	}()

	// The pointer is a linear memory offset, which is where we write the name.
	if bytes, ok := mod.Memory().Read(resultPtr, resultSize); !ok {
		log.Panicf("Memory.Read(%d, %d) out of range of memory size %d", resultSize, resultSize, mod.Memory().Size())
	} else {
		fmt.Println(string(bytes))
	}
}
