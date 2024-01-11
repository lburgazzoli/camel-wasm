#!/bin/sh

PROJECT_ROOT="$1"
IN="$2"
OUT="$3"

docker run \
		--rm \
		--user "$(id -u):$(id -g)" \
		-v "${PROJECT_ROOT}":/src:Z \
		-w /src \
		tinygo/tinygo:0.30.0 \
		tinygo build \
			-target=wasm \
			-scheduler=none \
			-o "${OUT}" \
			"${IN}"