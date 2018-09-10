package com.hmlr.api.models

import net.corda.core.identity.Party

/**
 * @author Nitesh Solanki
 */

fun Party.toSimpleName(): String {
    return "${name.organisation} (${name.locality}, ${name.country})"
}

