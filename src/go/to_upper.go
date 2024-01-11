package main

// main is required for TinyGo to compile to Wasm.
func main() {}

//export process
func _process(ptr, size uint32) uint64 {
	return 0
}
