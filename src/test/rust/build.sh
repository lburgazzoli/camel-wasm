#! /bin/bash
set -euxo pipefail

# rustup target add wasm32-unknown-unknown

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

echo $SCRIPT_DIR

rustc ${SCRIPT_DIR}/to_upper.rs \
  --target=wasm32-unknown-unknown \
  --crate-type=cdylib \
  -C opt-level=0 \
  -C debuginfo=0 \
  -o ${SCRIPT_DIR}/../../test/resources/to_upper.wasm
