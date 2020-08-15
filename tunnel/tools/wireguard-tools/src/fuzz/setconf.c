/*
 */

#include <stdio.h>
#undef stderr
#define stderr stdin
#define RUNSTATEDIR "/var/empty"
#include "../curve25519.c"
#define parse_allowedips parse_allowedips_ipc
#include "../ipc.c"
#undef parse_allowedips
#include "../encoding.c"
#include "../config.c"
static FILE *hacked_fopen(const char *pathname, const char *mode);
#define fopen hacked_fopen
#include "../setconf.c"
#undef fopen
#undef stderr

#include <string.h>
#include <stdlib.h>
#include <assert.h>

const char *__asan_default_options()
{
	return "verbosity=1";
}

const char *PROG_NAME = "wg";

struct hacked_pointers {
	const char *data;
	size_t data_len;
};

static FILE *hacked_fopen(const char *pathname, const char *mode)
{
	struct hacked_pointers *h = (struct hacked_pointers *)strtoul(pathname, NULL, 10);
	return fmemopen((char *)h->data, h->data_len, "r");
}

int LLVMFuzzerTestOneInput(const char *data, size_t data_len)
{
	char strptr[32];
	char *argv[3] = { "setconf", "wg0", strptr };
	struct hacked_pointers h = { data, data_len };

	snprintf(strptr, sizeof(strptr), "%lu", (unsigned long)&h);
	setconf_main(3, argv);
	return 0;
}
