use std::mem;
use std::slice;
use std::collections::HashMap;

use serde::{Deserialize, Serialize};
use base64_serde::base64_serde_type;

base64_serde_type!(Base64Standard, base64::engine::general_purpose::STANDARD);

#[derive(Serialize, Deserialize)]
struct Message {
    headers: HashMap<String, serde_json::Value>,

    #[serde(with = "Base64Standard")]
    body: Vec<u8>,
}

#[no_mangle]
pub extern "C" fn alloc(len: i32) -> *const u8 {
    let mut buf = Vec::with_capacity(len as usize);
    let ptr = buf.as_mut_ptr();
    // tell Rust not to clean this up
    mem::forget(buf);

    return ptr
}

#[no_mangle]
pub unsafe extern "C" fn dealloc(ptr: &mut u8, len: i32) {
    let _ = Vec::from_raw_parts(ptr, 0, len as usize);
}

#[no_mangle]
pub extern fn process(ptr: u32, len: u32) -> u64 {
    let bytes = unsafe { slice::from_raw_parts_mut(ptr as *mut u8, len as usize) };

    let mut msg: Message = serde_json::from_slice(bytes).unwrap();
    msg.body = String::from_utf8(msg.body).unwrap().to_uppercase().as_bytes().to_vec();

    let out_vec = serde_json::to_vec(&msg).unwrap();
    let out_ptr = out_vec.as_ptr() as u32;
    let out_len = out_vec.len() as u32;

    mem::forget(out_vec);

    return ((out_ptr as u64) << 32) | out_len as u64;
}
